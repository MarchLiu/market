package liu.mars.market.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TradeItem {
    private long makerId;
    private BigDecimal price;
    private Long turnover;

    @JsonProperty("maker-id")
    public long getMakerId() {
        return makerId;
    }

    public void setMakerId(long makerId) {
        this.makerId = makerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public BigDecimal getAmount() {
        return this.price.multiply(BigDecimal.valueOf(turnover));
    }
}
