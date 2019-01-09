package liu.mars.market.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.util.Timeout;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.market.dash.*;
import liu.mars.market.directive.LoadStatus;
import liu.mars.market.error.InLoading;
import liu.mars.market.error.LoadFailed;
import liu.mars.market.messages.*;
import liu.mars.market.status.DashStatus;
import liu.mars.market.status.Loading;
import liu.mars.market.status.Trading;
import liu.mars.market.trade.Trade;
import liu.mars.market.trade.TradeItem;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static akka.pattern.Patterns.ask;

public class DashActor extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static final String depth_namespace = "liu.mars.depth";
    static {
        CR.require(depth_namespace);
    }

    private AbstractActor.Receive trading = ReceiveBuilder.create()
            .match(StatusQuery.class, msg -> {
                sender().tell(new Trading(), self());
            })
            .match(LimitAsk.class, order -> {
                sender().tell(trade(order), self());
            })
            .match(LimitBid.class, msg -> {
                sender().tell(trade(msg), self());
            })
            .match(MarketAsk.class, msg -> {
                sender().tell(trade(msg), self());
            })
            .match(MarketBid.class, msg -> {
                sender().tell(trade(msg), self());
            })
            .match(Cancel.class, msg -> {
                sender().tell(trade(msg), self());
            })
            .match(QueryDepth.class, msg -> {
                sender().tell(queryDepth(msg), self());
            }).build();
    private AbstractActor.Receive loading = ReceiveBuilder.create()
            .match(StatusQuery.class, msg -> {
                sender().tell(new Loading(), self());
            })
            .match(DashStatus.class, status -> {
                log.info("received status {}", status.getId());
                this.askList.addAll(status.getAskList());
                this.bidList.addAll(status.getBidList());
                latestId = status.getLatestOrderId();
                switch (status.getStatus()) {
                    case "trading":
                        this.getContext().become(this.trading);
                        break;
                    default:
                        throw new LoadFailed(this.symbol, String.format("invalid status %s", status.getStatus()));
                }
            })
            .matchAny(msg -> {
                sender().tell(new InLoading(this.symbol), self());
            }).build();

    private String statusActorUrl;
    private IFn merge_depth = CR.var(depth_namespace, "merge-depth").fn();
    private LinkedList<Ask> askList = new LinkedList<>();
    private LinkedList<Bid> bidList = new LinkedList<>();
    private String symbol;
    private long latestId;

    private DashActor(String symbol, String statusActor) {
        this.symbol = symbol;
        this.statusActorUrl = statusActor;
    }

    public static Props props(String symbol, String statusActor) {
        return Props.create(DashActor.class, () -> new DashActor(symbol, statusActor));
    }

    public String getSymbol() {
        return symbol;
    }

    public long getLatestId() {
        return latestId;
    }

    public List<Bid> getBidList() {
        return bidList;
    }

    public List<Ask> getAskList() {
        return askList;
    }

    private Trade createTrade(Order order) throws Exception {
        Trade re = new Trade();
        re.setId(getNextId());
        re.setTakerId(order.getId());
        re.setSymbol(order.getSymbol());
        return re;
    }

    private Trade trade(MarketAsk order) throws Exception {
        latestId = order.getId();
        Trade re = createTrade(order);
        re.setTakeDirection("ask");
        while (order.getSurplus()>0){
            Bid bid = bidList.getLast();
            bid.trade(order).ifPresent(re::add);
            if(bid.getSurplus()==0){
                bidList.removeLast();
            }
        }
        return re;
    }

    private Trade trade(MarketBid order) throws Exception {
        latestId = order.getId();
        Trade re = createTrade(order);
        re.setTakeDirection("bid");
        while (order.getSurplus() > 0) {
            Ask ask = askList.getLast();
            ask.trade(order).ifPresent(re::add);
            if(ask.getSurplus()==0){
                askList.removeLast();
            }
        }

        return re;
    }

    private Trade trade(LimitAsk order) throws Exception {
        latestId = order.getId();
        Trade re = createTrade(order);
        re.setTakeDirection("ask");

        while (order.getSurplus()>0 && !bidList.isEmpty()){
            Bid bid = bidList.getLast();
            if (bid.getPrice().compareTo(order.getPrice()) < 0){
                break;
            }

            bid.trade(order).ifPresent(re::add);
            if(bid.getSurplus()==0){
                bidList.removeLast();
            }
        }

        if(order.getSurplus() > 0){
            askList.addLast(Ask.from(order));
        }

        return re;
    }

    private Trade trade(LimitBid order) throws Exception {
        latestId = order.getId();
        Trade re = createTrade(order);
        re.setTakeDirection("bid");

        while (order.getSurplus()>0 && !askList.isEmpty()){
            Ask ask = askList.getLast();
            if (ask.getPrice().compareTo(order.getPrice()) > 0){
                break;
            }

            ask.trade(order).ifPresent(re::add);
            if(ask.getSurplus()==0){
                askList.removeLast();
            }
        }
        if(order.getSurplus() > 0){
            bidList.addLast(Bid.from(order));
        }

        return re;
    }

    private Trade trade(Cancel order) throws Exception {
        latestId = order.getId();
        Trade re = createTrade(order);
        re.setTakeDirection("cancel");
        Predicate<Make> checker = make -> {
            if(make.getOrderId() == order.getOrderId()) {
                TradeItem item = new TradeItem();
                item.setPrice(make.getPrice());
                item.setMakerId(make.getOrderId());
                return true;
            }else {
                return false;
            }
        };
        askList.removeIf(checker);
        bidList.removeIf(checker);
        return re;
    }

    private void place(LimitAsk order) {
        latestId = order.getId();
        int idx = 0;
        while (idx<this.askList.size()){
            if(this.askList.get(idx).getPrice().compareTo(order.getPrice())<0){
                break;
            }
        }

        this.askList.add(idx, Ask.from(order));
    }

    private void place(LimitBid order) {
        latestId = order.getId();
        int idx = 0;
        while (idx<this.askList.size()){
            if(this.bidList.get(idx).getPrice().compareTo(order.getPrice())>0){
                break;
            }
        }

        this.bidList.add(idx, Bid.from(order));
    }

    private long getNextId() throws Exception {
        ActorRef seq = getContext().actorOf(Props.create(SequenceActor.class));

        Future future = ask(seq, "trade", 1000);
        Timeout timeout = Timeout.create(Duration.ofSeconds(1));
        return  (Long) Await.result(future, timeout.duration());
    }

    private List<Level> mergeLevel(int step, List<? extends Make> orders){
        return (List<Level>) merge_depth.invoke(step, orders);
    }

    private Depth queryDepth(QueryDepth query){
        String channel = String.format("%s.depth.step%d", symbol, query.getStep());
        Depth result = new Depth();
        result.setChannel(channel);
        result.setTs(LocalDateTime.now());
        result.setAsk(mergeLevel(query.getStep(), askList));
        result.setBid(mergeLevel(query.getStep(), bidList));
        result.setVersion(latestId);
        return result;
    }

    @Override
    public Receive createReceive() {
        getContext().actorSelection(statusActorUrl).tell(new LoadStatus(), self());
        return loading;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }


}
