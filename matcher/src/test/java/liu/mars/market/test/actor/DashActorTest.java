package liu.mars.market.test.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import liu.mars.dash.Depth;
import liu.mars.message.QueryDepth;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DashActorTest {
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testBasicLimitBid() throws InterruptedException {
        new TestKit(system) {{
            final String symbol = "ethbtc";
            final ActorRef mock = system.actorOf(StatusActorMock.props(symbol), "status");
            ActorRef dashActor = system.actorOf(
                    DashActor.props(symbol, String.format("akka://%s/user/status", system.name())),
                    "dash:"+symbol);
            Thread.sleep(1000);
            QueryDepth message = new QueryDepth();
            message.setStep(0);
            dashActor.tell(message, getRef());
            awaitCond(this::msgAvailable);
            expectMsgClass(Depth.class);
        }};
        Thread.sleep(1000);
    }
}
