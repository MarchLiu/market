package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.market.messages.FindOrder;
import liu.mars.market.messages.NextOrder;

public class PeekActor extends AbstractActor {
    private static final String order_namespace = "liu.mars.market.order";
    private IFn next;
    private IFn find;
    static  {
        CR.require(order_namespace);
    }

    public static Props props() {
        return Props.create(PeekActor.class, PeekActor::new);
    }

    private PeekActor() {
        this.find = CR.var(order_namespace, "find-by");
        this.next = CR.var(order_namespace, "find-next");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(NextOrder.class, msg -> {
            sender().tell(next.invoke(msg.getPositionId()), self());
        }).match(FindOrder.class, msg -> {
            sender().tell(find.invoke(msg.getId()), self());
        }).build();
    }
}
