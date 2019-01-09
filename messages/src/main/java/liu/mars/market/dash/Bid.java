package liu.mars.market.dash;

import clojure.lang.APersistentMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import liu.mars.market.messages.*;
import liu.mars.market.trade.TradeItem;

import java.io.IOException;
import java.util.Optional;

public final class Bid extends Make {

    public Bid(){}

    private Bid(LimitBid order){
        super(order);
    }

    private Bid(APersistentMap map) {
        super(map);
    }

    private Bid(String json) throws IOException {
        super(json);
    }

    @JsonIgnore
    public Optional<TradeItem> trade(LimitAsk order) {
        if (order.getPrice().compareTo(getPrice()) < 0) {
            return Optional.empty();
        }
        return super.trade(order);
    }

    @JsonIgnore
    public Optional<TradeItem> trade(MarketAsk order) {
        return super.trade(order);
    }

    public static Bid from(LimitBid order){
        return new Bid(order);
    }

    public static Bid from(APersistentMap map){
        return new Bid(map);
    }

    public static Bid from(String json) throws IOException {
        return new Bid(json);
    }
}
