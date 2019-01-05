package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import liu.mars.market.messages.NextValue;

import java.io.IOException;

public class SequencesApp extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(this.context().system(), this.getClass());

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(Long.class, msg -> {
            log.info("received long: {}", msg);
        }).build();
    }

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("sequences");
        var seqRef = system.actorOf(SequencesActor.props(), "sequences");
        System.out.println("Ctrl+c if want to stop");
    }
}
