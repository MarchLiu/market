package liu.mars.market;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import clojure.lang.IFn;
import jaskell.util.CR;
import liu.mars.market.dash.Depth;
import liu.mars.market.dash.Level;
import liu.mars.market.dash.Make;
import liu.mars.market.status.DashStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

public class RouteActor extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static final String depth_namespace = "liu.mars.market.depth";
    static {
        CR.require(depth_namespace);
    }

    private IFn merge_depth = CR.var(depth_namespace, "merge-depth").fn();

    static Props props(String symbol) {
        return Props.create(RouteActor.class, RouteActor::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(DashStatus.class, status -> {

        }).build();
    }
    private List<Level> mergeLevel(int step, List<? extends Make> orders){
        return (List<Level>) merge_depth.invoke(step, orders);
    }

    private void genDepth(DashStatus status){
        IntStream.of(0, 1, 2, 3, 4, 5).forEach( step -> {
            String channel = String.format("%s.depth.step%d", status.getSymbol(), step);
            Depth result = new Depth();
            result.setChannel(channel);
            result.setTs(LocalDateTime.now());
            result.setAsk(mergeLevel(step, status.getAskList()));
            result.setBid(mergeLevel(step, status.getBidList()));
            result.setVersion(status.getLatestOrderId());
            QuotationsBus.getInstance()
                    .publish(new QuotationsEvent(QuotationsBus.getTopic(channel), result));
        });
    }
}
