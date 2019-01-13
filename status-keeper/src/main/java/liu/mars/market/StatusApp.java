package liu.mars.market;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import liu.mars.market.status.DashStatus;

public class StatusApp extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(DashStatus.class, msg -> {

                }).match().build();
    }

    public static void main(String[] args){

    }
}
