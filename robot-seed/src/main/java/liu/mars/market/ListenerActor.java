package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import liu.mars.market.dash.Depth;

public class ListenerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(this);
    private ActorRef mediator;
    Cluster cluster = Cluster.get(getContext().getSystem());

    public static Props props(){
        return Props.create(ListenerActor.class, ListenerActor::new);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);
        mediator = DistributedPubSub.get(getContext().system()).mediator();
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ClusterEvent.MemberUp.class, msg -> {
            log.info("Member is Up: {}", msg.member());
        }).match(ClusterEvent.UnreachableMember.class, msg -> {
            log.info("Member detected as unreachable: {}", msg.member());
        }).match(ClusterEvent.MemberRemoved.class, msg -> {
            log.info("Member is Removed: {}", msg.member());
        }).match(ClusterEvent.MemberEvent.class, msg -> {
            log.info("Member event from {} : {}", msg.member(), msg);
        }).match(Depth.class, msg -> {
            mediator.tell(new DistributedPubSubMediator.Publish(msg.getChannel(), msg), self());
        }).build();
    }
}
