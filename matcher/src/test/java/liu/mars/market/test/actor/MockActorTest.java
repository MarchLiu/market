package liu.mars.market.test.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import liu.mars.directive.LoadStatus;
import liu.mars.status.DashStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

public class MockActorTest {

    private static ActorSystem system;
    private static ActorRef mock;
    final static String symbol = "btcusdt";

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("test");
        mock = system.actorOf(Props.create(StatusActorMock.class, symbol), "status");

    }

    @AfterClass
    public static void teardown() {
        system.stop(mock);
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testMock() {
        new TestKit(system) {{
            mock.tell(new LoadStatus(), getRef());

            // the run() method needs to finish within 3 seconds
            within(Duration.ofSeconds(5), () -> {
                mock.tell(new LoadStatus(), getRef());
                expectMsgPF("should a init status loaded", msg -> {
                    final var status = (DashStatus)msg;
                    Assert.assertEquals(0, status.getId());
                    Assert.assertEquals(0, status.getAskList().size());
                    Assert.assertEquals(0, status.getBidList().size());
                    Assert.assertEquals(symbol, status.getSymbol());
                    status.getAskList().stream().reduce((current, next) -> {
                        Assert.assertTrue(current.getPrice().compareTo(next.getPrice()) >= 0);
                        return next;
                    });
                    status.getBidList().stream().reduce((current, next) -> {
                        Assert.assertTrue(current.getPrice().compareTo(next.getPrice()) <= 0);
                        return next;
                    });
                    return msg;
                });
                return null;
            });
        }};
    }

    @Test
    public void testEmptyMock() {
        new TestKit(system) {{
            final TestKit probe = new TestKit(system);

            within(Duration.ofSeconds(15), () -> {
                mock.tell(new LoadStatus(), getRef());
                expectMsgPF("should a empty dash init", msg -> {
                    var empty = (DashStatus)msg;
                    Assert.assertEquals(0, empty.getId());
                    Assert.assertEquals(symbol, empty.getSymbol());
                    Assert.assertEquals(0, empty.getAskList().size());
                    Assert.assertEquals(0, empty.getBidList().size());
                    return empty;
                });
                return null;
            });
        }};
    }

    @Test
    public void testBasicLoad() {
        new TestKit(system) {{
            final TestKit probe = new TestKit(system);
            mock.tell(new StatusActorMock.PrepareStatus0("trading", symbol), ActorRef.noSender());

            final var dashRef = Props.create(DashActor.class,
                    symbol, String.format("akka://%s/user/status", system.name()));

            within(Duration.ofSeconds(5), () -> {
                // mock.tell(new LoadStatus(), getRef());
                system.actorSelection(String.format("akka://%s/user/status", system.name()))
                        .tell(new LoadStatus(), getRef());

                expectMsgPF("should a init status loaded", msg -> {
                    final var status = (DashStatus)msg;
                    Assert.assertEquals(1, status.getId());
                    Assert.assertEquals(symbol, status.getSymbol());
                    Assert.assertEquals(100, status.getLatestOrderId());
                    Assert.assertEquals(10, status.getAskList().size());
                    Assert.assertEquals(10, status.getBidList().size());
                    Assert.assertEquals(symbol, status.getSymbol());
                    status.getAskList().stream().reduce((current, next) -> {
                        Assert.assertTrue(current.getPrice().compareTo(next.getPrice()) >= 0);
                        return next;
                    });
                    status.getBidList().stream().reduce((current, next) -> {
                        Assert.assertTrue(current.getPrice().compareTo(next.getPrice()) <= 0);
                        return next;
                    });
                    return msg;
                });
                return null;
            });
        }};
    }

}
