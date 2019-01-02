package liu.mars.market;

import clojure.lang.*;
import com.esotericsoftware.kryo.Kryo;
import com.fasterxml.jackson.databind.node.*;
import liu.mars.market.messages.*;

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
