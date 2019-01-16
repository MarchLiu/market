package liu.mars.market;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import clojure.lang.IFn;
import com.fasterxml.jackson.databind.ObjectMapper;
import jaskell.util.CR;
import liu.mars.market.directive.LoadStatus;
import liu.mars.market.directive.StatusDump;
import liu.mars.market.directive.StatusQuery;
import liu.mars.market.status.DashStatus;

import java.time.Duration;

public class StatusApp extends AbstractActorWithTimers {
    private static String status_namespace = "liu.mars.market.status";
    static {
        CR.require(status_namespace);
    }
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private IFn save;
    private IFn load_latest;
    private ObjectMapper mapper;

    private StatusApp(){
        this.save = CR.var(status_namespace, "save").fn();
        this.load_latest = CR.var(status_namespace, "load-latest").fn();
        this.mapper = new ObjectMapper();
    }

    public static Props props() {
        return Props.create(StatusApp.class, StatusApp::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(DashStatus.class, msg -> {
                    String data = mapper.valueToTree(msg).toString();
                    save.invoke(data);
                    log.info("received status from {}", getSender().toString());
                }).match(LoadStatus.class, msg -> {
                    String result;
                    if (msg.getSymbol() == null){
                        result = (String) load_latest.invoke();
                    } else {
                        result = (String) load_latest.invoke(msg.getSymbol());
                    }
                    DashStatus status = mapper.readValue(result, DashStatus.class);
                    log.info("load status {} for load request from {}",
                            result, getSender().toString());
                    sender().tell(status, self());
                }).build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    public static void main(String[] args){
        final String config_namespace = "liu.mars.market.config";
        CR.require(config_namespace);
        ActorSystem system = ActorSystem.create("status");
        ActorRef statusActor = system.actorOf(StatusApp.props(), "status");
        LoggingAdapter log = Logging.getLogger(system, system.scheduler());
        long query_rate = (Long)CR.invoke(config_namespace, "query-rate");
        Cancellable schedule = system.scheduler().schedule(Duration.ofSeconds(query_rate),
                Duration.ofSeconds(60), () -> {
            String matcher_path = CR.invoke(config_namespace, "matcher").toString();
            ActorSelection matcher = system.actorSelection(matcher_path);
            StatusDump query = new StatusDump();
            query.setSymbol("btcusdt");
            matcher.tell(query, statusActor);
            log.info("status query to {}", matcher_path);
        }, system.dispatcher());

        system.registerOnTermination(() -> {
            system.stop(statusActor);
            schedule.cancel();
        });


        System.out.println("Ctrl+c to stop");
    }
}
