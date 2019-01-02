package liu.mars.market;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.market.messages.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RemoteTest {
    private static String path = "akka.tcp://counter@192.168.50.22:25530/user/order";

    private static ActorSystem system;
    private static ActorRef server;
    private Random random = new Random();

    @BeforeClass
    public static void setup() throws InterruptedException {
        system = ActorSystem.create("test");
    }

    @AfterClass
    public static void teardown() throws InterruptedException {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testBasicFlow() {
        new TestKit(system){{
            ActorSelection remote = system.actorSelection(path);
            AtomicLong lastId = new AtomicLong();
            remote.tell(randOrder(), getRef());
            awaitCond(this::msgAvailable);
            expectMsgPF("check first id and save it", msg -> {
                Assert.assertTrue(msg instanceof Long);
                lastId.set((Long) msg);
                return msg;
            });
            for(int i = 0; i<100; i++){
                remote.tell(randOrder(), getRef());
                awaitCond(this::msgAvailable);
                expectMsgPF(String.format("check id %d times", i), msg -> {
                    Assert.assertEquals(lastId.incrementAndGet(), msg);
                    return msg;
                });
            }
        }};
    }

    private Order randOrder() {
        int dice = random.nextInt(5);
        switch (dice) {
            case 0:
                return randLimitAsk();
            case 1:
                return randLimitBid();
            case 2:
                return randMarketAsk();
            case 3:
                return randMarketBid();
            case 4:
                return randCancel();
            default:
                return null;
        }
    }

    private LimitAsk randLimitAsk(){
        LimitAsk result = new LimitAsk();
        result.setSymbol("btcusdt");
        result.setAccountId(random.nextLong());
        result.setPrice(BigDecimal.valueOf(random.nextDouble()));
        result.setQuantity(random.nextLong());
        return result;
    }

    private LimitBid randLimitBid(){
        LimitBid result = new LimitBid();
        result.setSymbol("btcusdt");
        result.setAccountId(random.nextLong());
        result.setPrice(BigDecimal.valueOf(random.nextDouble()));
        result.setQuantity(random.nextLong());
        return result;
    }

    private MarketAsk randMarketAsk(){
        MarketAsk result = new MarketAsk();
        result.setSymbol("btcusdt");
        result.setAccountId(random.nextLong());
        result.setQuantity(random.nextLong());
        return result;
    }

    private MarketBid randMarketBid(){
        MarketBid result = new MarketBid();
        result.setSymbol("btcusdt");
        result.setAccountId(random.nextLong());
        result.setQuantity(random.nextLong());
        return result;
    }

    private Cancel randCancel(){
        Cancel result = new Cancel();
        result.setSymbol("btcusdt");
        result.setAccountId(random.nextLong());
        return result;
    }
}
