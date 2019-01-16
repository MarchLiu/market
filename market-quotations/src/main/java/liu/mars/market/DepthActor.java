package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import liu.mars.market.status.DashStatus;

public class DepthActor extends AbstractActor {
    private String topic;
    public DepthActor(String topic){
        this.topic = topic;
    }

    static Props props(String topic) {
        return Props.create(DepthActor.class, () -> new DepthActor(topic));
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(DashStatus.class, status -> {

        }).build();
    }
}
