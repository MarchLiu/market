package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import liu.mars.market.dash.Depth;
import liu.mars.market.status.DashStatus;

public class DepthActor extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private String topic;
    public DepthActor(String topic){
        this.topic = topic;
    }

    static Props props(String topic) {
        return Props.create(DepthActor.class, () -> new DepthActor(topic));
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(Depth.class, depth -> {
            log.info("depth {} from event bus", depth);
        }).build();
    }
}
