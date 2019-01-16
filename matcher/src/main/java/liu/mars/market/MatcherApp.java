package liu.mars.market;

import akka.actor.ActorSystem;
import liu.mars.market.actor.DashActor;
import liu.mars.market.messages.NextOrder;

import java.time.Duration;

public class MatcherApp {

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("match");
        system.actorOf(DashActor.props("btcusdt"), "btcusdt");
        System.out.println("waiting 5 seconds for status loading.");
        Thread.sleep(5000);
        System.out.println("Ctrl+c to stop");
    }
}
