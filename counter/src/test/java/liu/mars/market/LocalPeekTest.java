package liu.mars.market;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class LocalPeekTest {
    private static ActorSystem system;
    private static ActorRef server;

    @BeforeClass
    public static void setup() throws InterruptedException {
        system = ActorSystem.create("test");
    }

    @AfterClass
    public static void teardown() throws InterruptedException {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
}
