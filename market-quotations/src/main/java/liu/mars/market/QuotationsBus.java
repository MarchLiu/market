package liu.mars.market;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.japi.ManagedActorEventBus;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

public class QuotationsBus extends ManagedActorEventBus<QuotationsEvent> {
    static private QuotationsBus instance;

    static void init(ActorSystem system) {
        instance = new QuotationsBus(system);
        Stream.of("market.btcusdt.depth0",
                "market.btcusdt.depth1",
                "market.btcusdt.dpeth2",
                "market.btcusdt.dpeth3",
                "market.btcusdt.dpeth4",
                "market.btcusdt.dpeth5").forEach(topic -> {
            topics.put(topic, system.actorOf(DepthActor.props(topic)));
        });
    }

    static private HashMap<String, ActorRef> topics = new HashMap<>();

    private QuotationsBus(ActorSystem system) {
        super(system);
    }

    static public ActorRef getTopic(String topic) {
        return topics.get(topic);
    }

    static public Set<String> topicNames() {
        return topics.keySet();
    }

    @Override
    public int mapSize() {
        return 128;
    }

    @Override
    public ActorRef classify(QuotationsEvent event) {
        return event.topicActor;
    }

    public boolean subscribe(ActorRef subscriber, String topic) {
        return this.subscribe(subscriber, QuotationsBus.getTopic(topic));
    }

    static public QuotationsBus getInstance() {
        return instance;
    }
}
