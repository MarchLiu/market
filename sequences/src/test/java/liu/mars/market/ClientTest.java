package liu.mars.market;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import liu.mars.market.messages.CreateSequence;
import liu.mars.market.messages.DropSequence;
import liu.mars.market.messages.NextValue;
import org.junit.*;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTest {
    private static ActorSystem system;
    private static ActorRef server;

    @BeforeClass
    public static void setup() throws InterruptedException {
        system = ActorSystem.create("test");
        server = system.actorOf(SequencesActor.props(), "sequences");
        var message = new CreateSequence();
        message.setName("test");
        server.tell(message, server);
        Thread.sleep(1000);
    }

    @AfterClass
    public static void teardown() throws InterruptedException {
        var message = new DropSequence();
        message.setName("test");
        server.tell(message, server);
        Thread.sleep(1000);
        TestKit.shutdownActorSystem(system);
        server = null;
        system = null;
    }

    @Test
    public void testNextValue() {
        new TestKit(system) {{
            var lastId = new AtomicLong();
            ActorSelection server = system.actorSelection("akka://test/user/sequences");
            var message = new NextValue();
            message.setName("test");
            server.tell(message, getRef());
            awaitCond(this::msgAvailable);
            expectMsgPF("init test data", msg -> {
                lastId.set((Long) msg);
                return msg;
            });

            within(Duration.ofSeconds(5), () -> {
                for (int i = 0; i < 10; i++) {
                    server.tell(message, getRef());
                    awaitCond(this::msgAvailable);
                    expectMsgPF("expect get next and next values", msg -> {
                        Assert.assertEquals(lastId.get()+1, msg);
                        lastId.set((Long)msg);
                        return msg;
                    });
                }
                return null;
            });
        }};
    }

    @Test
    public void testNextValueFromRemote() {
        new TestKit(system) {{
            var lastId = new AtomicLong();
            ActorSelection server = system.actorSelection("akka.tcp://test@127.0.0.1:25520/user/sequences");
            var message = new NextValue();
            message.setName("test");
            server.tell(message, getRef());
            awaitCond(this::msgAvailable);
            expectMsgPF("init test data", msg -> {
                lastId.set((Long) msg);
                return msg;
            });

            within(Duration.ofSeconds(5), () -> {
                for (int i = 0; i < 10; i++) {
                    server.tell(message, getRef());
                    awaitCond(this::msgAvailable);
                    expectMsgPF("expect get next and next values", msg -> {
                        Assert.assertEquals(lastId.get()+1, msg);
                        lastId.set((Long)msg);
                        return msg;
                    });
                }
                return null;
            });
        }};
    }
}
