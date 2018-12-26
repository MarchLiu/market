package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class SequencesActor extends AbstractActor {
    private long orderId = 0;

    public static Props props() {
        return Props.create(SequencesActor.class, SequencesActor::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().matchEquals("next", msg -> {
            sender().tell(++orderId, self());
        }).build();
    }
}
