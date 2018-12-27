package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.market.messages.CreateSequences;
import liu.mars.market.messages.ListSequences;
import liu.mars.market.messages.NextValue;

public class SequencesActor extends AbstractActor {
    private final static String seq_namespace = "liu.mars.market.seq";
    private static IFn creator;
    private static IFn nextVal;
    private static IFn listSeq;
    static {
        CR.require(seq_namespace);
        creator = CR.var(seq_namespace, "create-seq").fn();
        nextVal = CR.var(seq_namespace, "next-val").fn();
        listSeq = CR.var(seq_namespace, "list-seq").fn();
    }

    public static Props props() {
        return Props.create(SequencesActor.class, SequencesActor::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(CreateSequences.class, msg -> {
            sender().tell(creator.invoke(msg.getName()), self());
        }).match(NextValue.class, msg -> {
            sender().tell(nextVal.invoke(msg.getName()), self());
        }).match(ListSequences.class, msg -> {
            sender().tell(listSeq.invoke(), self());
        }).build();
    }
}
