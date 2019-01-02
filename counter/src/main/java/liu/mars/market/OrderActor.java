package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.market.messages.*;
import scala.concurrent.Await;

import java.time.Duration;

public class OrderActor extends AbstractActor {
    private static final String order_namespace = "liu.mars.market.order";
    private String url;
    private IFn placeOrder;
    static { CR.require(order_namespace);

    }
    private ActorSelection seqSelection;
    public static Props props(String url){
        return Props.create(OrderActor.class, ()-> new OrderActor(url));
    }

    private OrderActor(String url){
        this.url = url;
        placeOrder = CR.var(order_namespace, "place-order").fn();
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder().match(LimitAsk.class, msg -> {
            sender().tell(save(msg), self());
        }).match(LimitBid.class, msg -> {
            sender().tell(save(msg), self());
        }).match(MarketAsk.class, msg -> {
           sender().tell(save(msg), self());
        }).match(MarketBid.class, msg -> {
            sender().tell(save(msg), self());
        }).match(Cancel.class, msg -> {
            sender().tell(save(msg), self());
        }).build();
    }

    private long save(Order order) throws Exception {
        NextValue nextValue = new NextValue();
        nextValue.setName("order_id");
        Timeout timeout = Timeout.create(Duration.ofSeconds(1));
        var future = Patterns.ask(seqSelection, nextValue, timeout);
        long result = (Long)Await.result(future, timeout.duration());
        order.setId(result);
        placeOrder.invoke(order);
        return result;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        seqSelection = context().actorSelection(url);
    }
}
