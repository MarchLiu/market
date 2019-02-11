package liu.mars.market;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.client.ClusterClientSettings;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.testkit.javadsl.TestKit;
import liu.mars.market.dash.Depth;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListenerTest {
    public static class Listener extends AbstractActor {
        private String feedback;
        private Listener(String feedback){
            this.feedback = feedback;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().match(Depth.class, msg -> {
                this.context().system().actorSelection(String.format("/system/%s", feedback))
                        .tell(msg, self());
            }).build();
        }


        public static Props props(String feedback) {
            return Props.create(Listener.class, ()-> new Listener(feedback));
        }
    }

    private static ActorSystem system;
    private static Cluster cluster;
    private static String channel = "btcusdt.depth.step0";


    static private Set<ActorPath> initialContacts(Integer port){
        Set<ActorPath> re = new HashSet<>();
        re.add(ActorPaths.fromString(String.format("akka.tcp://market@192.168.50.83:%d/system/receptionist", port)));
        return re;
    }

    private Depth emptyDepth() {
        var re = new Depth();
        re.setAsk(new ArrayList<>());
        re.setBid(new ArrayList<>());
        re.setChannel(channel);
        re.setVersion(0);
        re.setTs(LocalDateTime.now());
        return re;
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        system = ActorSystem.create("market");
        cluster = Cluster.get(system);
    }

    @AfterClass
    public static void teardown() throws InterruptedException {
        cluster.leave(cluster.selfAddress());
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testPubSub() {
        new TestKit(system){{
            var mediator = DistributedPubSub.get(system).mediator();
            var self = getRef();

            mediator.tell(new DistributedPubSubMediator.Subscribe(channel, self), self);
            this.awaitCond(this::msgAvailable);
            this.expectMsgPF("expect subscribed ack", msg -> {
                Assert.assertTrue(msg instanceof DistributedPubSubMediator.SubscribeAck);
                return msg;
            });

            mediator.tell(new DistributedPubSubMediator.Publish(channel, emptyDepth()), self);
            this.awaitCond(this::msgAvailable);
            this.expectMsgPF("expect empty depth", msg -> {
                Assert.assertTrue(msg instanceof Depth);
                var depth = (Depth)msg;
                Assert.assertEquals(0, depth.getVersion());
                Assert.assertEquals(0, depth.getAsk().size());
                Assert.assertEquals(0, depth.getBid().size());
                Assert.assertEquals(channel, depth.getChannel());

                return msg;
            });


            mediator.tell(new DistributedPubSubMediator.Unsubscribe(channel, self), self);
            this.awaitCond(this::msgAvailable);
            this.expectMsgPF("expect unsubscribed ack", msg -> {
                Assert.assertTrue(msg instanceof DistributedPubSubMediator.UnsubscribeAck);
                return msg;
            });

        }};
    }

    @Test
    public void testClient() throws InterruptedException {
        new TestKit(system) {{
            var self = getRef();
            var receptionlist = ClusterClientReceptionist.get(system);
            var serviceActor = system.actorOf(Listener.props(self.path().name()), "service-client");
            Integer port = (Integer) system.provider().getDefaultAddress().port().get();
            var client = system.actorOf(
                    ClusterClient.props(ClusterClientSettings.create(system)
                            .withInitialContacts(initialContacts(port))));
            try {
                receptionlist.registerService(serviceActor);
                receptionlist.registerSubscriber(channel, self);
                Thread.sleep(1000);
                client.tell(new ClusterClient.Publish(channel, emptyDepth()), self);
                this.awaitCond(this::msgAvailable);
                expectMsgPF("expect empty depth from client publish", msg -> {
                    Assert.assertTrue(msg instanceof Depth);
                    var depth = (Depth) msg;
                    Assert.assertEquals(0, depth.getVersion());
                    Assert.assertEquals(0, depth.getAsk().size());
                    Assert.assertEquals(0, depth.getBid().size());
                    Assert.assertEquals(channel, depth.getChannel());
                    return msg;
                });

                client.tell(new ClusterClient.Send("/user/service-client", emptyDepth()), self);
                this.awaitCond(this::msgAvailable);
                expectMsgPF("expect empty depth from client publish", msg -> {
                    Assert.assertTrue(msg instanceof Depth);
                    var depth = (Depth) msg;
                    Assert.assertEquals(0, depth.getVersion());
                    Assert.assertEquals(0, depth.getAsk().size());
                    Assert.assertEquals(0, depth.getBid().size());
                    Assert.assertEquals(channel, depth.getChannel());
                    return msg;
                });
            }finally {
                receptionlist.unregisterService(serviceActor);
                receptionlist.unregisterSubscriber(channel, self);
            }
        }};
    }

    @Test
    public void testCross() {
        new TestKit(system) {{
            var self = getRef();

            var mediator = DistributedPubSub.get(system).mediator();
            var serviceActor = system.actorOf(Listener.props(self.path().name()), "service-cross");
            Integer port = (Integer) system.provider().getDefaultAddress().port().get();
            var client = system.actorOf(
                    ClusterClient.props(ClusterClientSettings.create(system)
                            .withInitialContacts(initialContacts(port))));

            mediator.tell(new DistributedPubSubMediator.Subscribe(channel, serviceActor), self);
            awaitCond(this::msgAvailable);
            expectMsgPF("expect subscribed ack", msg -> {
                Assert.assertTrue(msg instanceof DistributedPubSubMediator.SubscribeAck);
                return msg;
            });

            ClusterClientReceptionist.get(system).registerService(serviceActor);
            client.tell(new ClusterClient.Publish(channel, emptyDepth()), self);
            awaitCond(this::msgAvailable);
            expectMsgPF("expect empty depth from client publish", msg -> {
                Assert.assertTrue(msg instanceof Depth);
                var depth = (Depth)msg;
                Assert.assertEquals(0, depth.getVersion());
                Assert.assertEquals(0, depth.getAsk().size());
                Assert.assertEquals(0, depth.getBid().size());
                Assert.assertEquals(channel, depth.getChannel());
                return msg;
            });

            mediator.tell(new DistributedPubSubMediator.Publish(channel, emptyDepth()), self);
            this.awaitCond(this::msgAvailable);
            expectMsgPF("expect empty depth from client publish", msg -> {
                Assert.assertTrue(msg instanceof Depth);
                var depth = (Depth)msg;
                Assert.assertEquals(0, depth.getVersion());
                Assert.assertEquals(0, depth.getAsk().size());
                Assert.assertEquals(0, depth.getBid().size());
                Assert.assertEquals(channel, depth.getChannel());
                return msg;
            });


            mediator.tell(new DistributedPubSubMediator.Unsubscribe(channel, serviceActor), self);
            awaitCond(this::msgAvailable);
            expectMsgPF("expect unsubscribed ack", msg -> {
                Assert.assertTrue(msg instanceof DistributedPubSubMediator.UnsubscribeAck);
                return msg;
            });
        }};
    }

    @Test
    public void testRouter() throws InterruptedException {
        new TestKit(system) {{
            var self = getRef();
            var receptionlist = ClusterClientReceptionist.get(system);
            Integer port = 2558;
            var client = system.actorOf(
                    ClusterClient.props(ClusterClientSettings.create(system)
                            .withInitialContacts(initialContacts(port))));
            try {
                receptionlist.registerSubscriber(channel, self);
                Thread.sleep(1000);
                client.tell(new ClusterClient.Publish(channel, emptyDepth()), self);
                this.awaitCond(this::msgAvailable);
                expectMsgPF("expect empty depth from client publish", msg -> {
                    Assert.assertTrue(msg instanceof Depth);
                    var depth = (Depth) msg;
                    Assert.assertEquals(0, depth.getVersion());
                    Assert.assertEquals(0, depth.getAsk().size());
                    Assert.assertEquals(0, depth.getBid().size());
                    Assert.assertEquals(channel, depth.getChannel());
                    return msg;
                });

                client.tell(new ClusterClient.Send("/user/seed", emptyDepth()), self);
                this.awaitCond(this::msgAvailable);
                expectMsgPF("expect empty depth from client publish", msg -> {
                    Assert.assertTrue(msg instanceof Depth);
                    var depth = (Depth) msg;
                    Assert.assertEquals(0, depth.getVersion());
                    Assert.assertEquals(0, depth.getAsk().size());
                    Assert.assertEquals(0, depth.getBid().size());
                    Assert.assertEquals(channel, depth.getChannel());
                    return msg;
                });
            }finally {
                receptionlist.unregisterSubscriber(channel, self);
            }
        }};
    }
}
