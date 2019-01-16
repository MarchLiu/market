package liu.mars.market;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import liu.mars.market.status.DashStatus;

public class MarketEvent {
    public final ActorRef topicActor;
    public final Object data;

    public MarketEvent(ActorRef topic, Object data) {
        this.topicActor = topic;
        this.data = data;
    }
}
