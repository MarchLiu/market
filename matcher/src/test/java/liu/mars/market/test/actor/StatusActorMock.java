package liu.mars.market.test.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.dash.Ask;
import liu.mars.dash.Bid;
import liu.mars.directive.LoadStatus;
import liu.mars.message.LimitAsk;
import liu.mars.message.LimitBid;
import liu.mars.status.DashStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StatusActorMock extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static final String dash_namespace = "liu.mars.test.dash";

    static {
        CR.require(dash_namespace);
    }

    public static Props props(String symbol){
        return Props.create(StatusActorMock.class, () -> new StatusActorMock(symbol));
    }

    private IFn status0;
    private IFn empty;

    private StatusActorMock(String symbol){
        this.symbol = symbol;
        this.status0 = CR.var(dash_namespace, "status-0").fn();
        this.empty = CR.var(dash_namespace, "empty-status").fn();
        this.status = loadEmpty("trading", symbol);
    }

    private final String symbol;

    private Random rand = new Random();
    private DashStatus status;
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(LoadStatus.class, msg -> {
                    sender().tell(status, self());
                }).match(PrepareEmpty.class, msg -> {
                    var result = loadEmpty(msg.getStatus(), msg.getSymbol());
                    status = result;
                    sender().tell(result, self());
                }).match(PrepareStatus0.class, msg -> {
                    var result = load0(msg.getStatus(), msg.getSymbol());
                    status = result;
                    sender().tell(result, self());
                }).match(PrepareBasic.class, msg -> {
                    var result = loadBasic(msg.getStatus(), msg.getSymbol(), msg.getDepth());
                    status = result;
                    sender().tell(result, self());
                }).build();
    }


    private DashStatus loadEmpty(String status, String symbol){
        return (DashStatus) this.empty.invoke(status, symbol);
    }

    private DashStatus load0(String status, String symbol) {
        return (DashStatus) this.status0.invoke(status, symbol);
    }

    private DashStatus loadBasic(String status, String symbol, int depth){
        var result = new DashStatus();
        result.setId(0);
        result.setLatestOrderId(21);
        List<Ask> asks = newAskList(depth, symbol);
        List<Bid> bids = newBidList(depth, symbol);
        result.setAskList(asks);
        result.setBidList(bids);
        result.setSymbol(symbol);
        result.setStatus(status);
        return result;
    }

    private Ask newAsk(long id, String symbol) {
        LimitAsk order = new LimitAsk();
        order.setId(id);
        order.setAccountId(rand.nextLong());
        order.setPrice(BigDecimal.valueOf(rand.nextInt(10000)+10000));
        order.setQuantity(rand.nextInt(20000));
        order.setSymbol(symbol);
        return Ask.from(order);
    }

    private List<Ask> newAskList(int count, String symbol) {
        List<Ask> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(newAsk(i, symbol));
        }
        result.sort(Comparator.comparing(Ask::getPrice).reversed());
        return result;
    }

    private Bid newBid(long id, String symbol) {
        LimitBid order = new LimitBid();
        order.setId(id);
        order.setAccountId(rand.nextLong());
        order.setPrice(BigDecimal.valueOf(rand.nextInt(10000)));
        order.setQuantity(rand.nextInt(20000));
        order.setSymbol(symbol);
        return Bid.from(order);
    }

    private List<Bid> newBidList(int count, String symbol) {
        List<Bid> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(newBid(i, symbol));
        }
        result.sort(Comparator.comparing(Bid::getPrice));

        return result;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    public static abstract class Prepare{
        private String status;
        private String symbol;

        Prepare(String status, String symbol){
            this.status = status;
            this.symbol = symbol;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }

    public static class PrepareEmpty extends Prepare {
        public PrepareEmpty(String status, String symbol) {
            super(status, symbol);
        }
    }

    public static class PrepareStatus0 extends Prepare {
        public PrepareStatus0(String status, String symbol) {
            super(status, symbol);
        }
    }

    public static class PrepareBasic extends Prepare {
        private int depth;
        public PrepareBasic(String status, String symbol, int depth) {
            super(status, symbol);
            this.depth = depth;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }
    }
}
