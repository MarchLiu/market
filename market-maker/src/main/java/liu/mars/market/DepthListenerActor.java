package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import liu.mars.market.dash.Depth;

public class DepthListenerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(this);
    Cluster cluster = Cluster.get(getContext().getSystem());

    public static Props props() {
        return Props.create(DepthListenerActor.class, DepthListenerActor::new);
    }

    private DepthListenerActor() {
        ActorRef mediator =
                DistributedPubSub.get(getContext().system()).mediator();
        // subscribe to the topic named "content"
        mediator.tell(new DistributedPubSubMediator.Subscribe("btcusdt.depth.step0", getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Depth.class, msg -> {
            log.info("received depth {}", msg);
        }).match(DistributedPubSubMediator.SubscribeAck.class, msg -> log.info("subscribed depth")).build();
    }
}
