package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;

public class WatchActor extends AbstractActor {
    private final ActorRef child = getContext().actorOf(Props.empty(), "target");
    private ActorRef lastSender = getContext().system().deadLetters();

    public WatchActor() {
        getContext().watch(child); // <-- this is the only call needed for registration
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("kill", s -> {
                    getContext().stop(child);
                    lastSender = getSender();
                })
                .match(Terminated.class, t -> t.actor().equals(child), t -> {
                    lastSender.tell("finished", getSelf());
                })
                .build();
    }
}