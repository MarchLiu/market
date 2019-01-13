package liu.mars.market;

import akka.actor.ActorSystem;
import jaskell.util.CR;

import java.io.IOException;

public class CounterApp {
    public static void main(String[] args) throws IOException {
        String config_namespace = "liu.mars.market.config";
        CR.require(config_namespace);
        String path = (String)CR.var(config_namespace, "sequences").get();
        ActorSystem system = ActorSystem.create("counter");
        var placeRef = system.actorOf(PlaceActor.props(path), "place");
        var peekRef = system.actorOf(PeekActor.props(), "peek");
        system.registerOnTermination(()->{
            system.stop(placeRef);
            system.stop(peekRef);
        });
        System.out.println("Ctrl+c to exit.");
    }
}
