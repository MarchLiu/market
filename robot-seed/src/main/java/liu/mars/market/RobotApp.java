package liu.mars.market;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.client.ClusterClientSettings;

public class RobotApp {
    public static void main(String[] args){
        ActorSystem system = ActorSystem.create("market");
        ActorRef seed = system.actorOf(ListenerActor.props(), "seed");
        Integer port = (Integer) system.provider().getDefaultAddress().port().get();
        var cluster = Cluster.get(system);
        var receptionlist = ClusterClientReceptionist.get(system);
        receptionlist.registerService(seed);
        System.out.println("Ctrl+c to stop...");
        system.registerOnTermination( () -> {
            receptionlist.unregisterService(seed);
            cluster.leave(cluster.selfAddress());
        });
    }
}
