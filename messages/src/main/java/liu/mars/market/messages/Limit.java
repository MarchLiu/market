package liu.mars.market.messages;

import java.math.BigDecimal;

public interface Limit extends TradeOrder {
    BigDecimal getPrice();
    BigDecimal getAmount();
}
