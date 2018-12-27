package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import liu.mars.market.messages.NextValue;

public class App extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(this.context().system(), this.getClass());

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(Long.class, msg -> {
            log.info("received long: {}", msg);
        }).build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("sequences");
        var seqRef = system.actorOf(SequencesActor.props(), "sequences");
        var mineRef = system.actorOf(Props.create(App.class), "ask");
        NextValue msg = new NextValue();
        msg.setName("orders");
        while (true) {
            seqRef.tell(msg, mineRef);
            Thread.sleep(500);
        }
    }
}
