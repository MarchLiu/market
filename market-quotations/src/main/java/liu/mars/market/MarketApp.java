package liu.mars.market;

import akka.actor.ActorSystem;

public class MarketApp {
    public static void main(String[] args){
        ActorSystem system = ActorSystem.create("market");
        QuotationsBus.init(system);
        system.actorOf(RouteActor.props("btcusdt"), "btcusdt");
        System.out.println("Ctrl+c to stop");
    }
}
