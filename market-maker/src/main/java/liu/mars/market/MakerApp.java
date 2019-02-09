package liu.mars.market;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class MakerApp {
    public static void main(String[] args){
        ActorSystem system = ActorSystem.create("market");
        ActorRef listener = system.actorOf(DepthListenerActor.props(), "maker");

    }
}
