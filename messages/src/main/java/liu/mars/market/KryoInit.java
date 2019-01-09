package liu.mars.market;

import clojure.lang.*;
import com.esotericsoftware.kryo.Kryo;
import com.fasterxml.jackson.databind.node.*;
import liu.mars.market.dash.*;
import liu.mars.market.directive.LoadOrders;
import liu.mars.market.directive.LoadStatus;
import liu.mars.market.directive.StatusQuery;
import liu.mars.market.error.InLoading;
import liu.mars.market.error.LoadFailed;
import liu.mars.market.messages.*;
import liu.mars.market.status.Trading;
import liu.mars.market.trade.Trade;
import liu.mars.market.trade.TradeItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class KryoInit {
    public void customize(Kryo kryo){
        kryo.register(BigDecimal.class);
        kryo.register(CreateSequence.class);
        kryo.register(DropSequence.class);
        kryo.register(ListSequences.class);
        kryo.register(NextValue.class);
        kryo.register(LimitAsk.class);
        kryo.register(LimitBid.class);
        kryo.register(MarketAsk.class);
        kryo.register(MarketBid.class);
        kryo.register(Cancel.class);
        kryo.register(NextOrder.class);
        kryo.register(FindOrder.class);
        kryo.register(OrderNoMore.class);
        kryo.register(OrderNotFound.class);
        kryo.register(LoadOrders.class);
        kryo.register(LoadStatus.class);
        kryo.register(StatusQuery.class);
        kryo.register(Ask.class);
        kryo.register(Bid.class);
        kryo.register(Depth.class);
        kryo.register(Level.class);
        kryo.register(Make.class);
        kryo.register(QueryDepth.class);
        kryo.register(LoadFailed.class);
        kryo.register(Trading.class);
        kryo.register(InLoading.class);
        kryo.register(Trade.class);
        kryo.register(TradeItem.class);
        kryo.register(ObjectNode.class);
        kryo.register(ArrayNode.class);
        kryo.register(LongNode.class);
        kryo.register(TextNode.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedHashMap.class);
        kryo.register(JsonNodeFactory.class);
        kryo.register(PersistentArrayMap.class);
        kryo.register(PersistentVector.class);
        kryo.register(PersistentList.class);
        kryo.register(Keyword.class);
        kryo.register(Symbol.class);
        kryo.register(LazySeq.class);
    }
}
