package liu.mars.market.dash;

import clojure.lang.APersistentMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import liu.mars.market.messages.LimitAsk;
import liu.mars.market.messages.LimitBid;
import liu.mars.market.messages.MarketBid;
import liu.mars.market.trade.TradeItem;

import java.io.IOException;
import java.util.Optional;

public final class Ask extends Make {

    public Ask(){}

    private Ask(LimitAsk ask) {
        super(ask);
    }

    private Ask(APersistentMap map) {
        super(map);
    }

    private Ask(String json) throws IOException {
        super(json);
    }

    @JsonIgnore
    public Optional<TradeItem> trade(LimitBid order) {
        if (order.getPrice().compareTo(getPrice()) < 0) {
            return Optional.empty();
        }
        return super.trade(order);
    }

    @JsonIgnore
    public Optional<TradeItem> trade(MarketBid order) {
        return super.trade(order);
    }

    public static Ask from(LimitAsk order){
        return new Ask(order);
    }

    public static Ask from(APersistentMap map) {
        return new Ask(map);
    }

    public static Ask from(String json) throws IOException {
        return new Ask(json);
    }
}
