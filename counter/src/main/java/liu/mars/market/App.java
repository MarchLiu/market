package liu.mars.market;

import akka.actor.ActorSystem;
import jaskell.util.CR;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        String config_namespace = "liu.mars.market.config";
        CR.require(config_namespace);
        String path = (String)CR.var(config_namespace, "seq").invoke();
        ActorSystem system = ActorSystem.create("counter");
        var seqRef = system.actorOf(OrderActor.props(path), "order");
        System.out.println("Ctrl+c if want to stop");
    }
}
