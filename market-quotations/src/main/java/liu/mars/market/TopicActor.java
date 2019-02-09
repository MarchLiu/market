package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class TopicActor extends AbstractActor {
    private String topic;
    public TopicActor(String topic){
        this.topic = topic;
    }

    static Props props(String topic) {
        return Props.create(TopicActor.class, () -> new TopicActor(topic));
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().matchEquals("topic", x -> {
            getSender().tell(topic, getSelf());
        }).build();
    }
}
