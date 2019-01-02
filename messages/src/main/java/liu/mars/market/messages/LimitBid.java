package liu.mars.market.messages;

import java.math.BigDecimal;

public class LimitBid implements Limit {
    private long id;
    private BigDecimal price;
    private long quantity;
    private long completed;
    private long accountId;
    private String symbol;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    @Override
    public long getSurplus() {
        return quantity - completed;
    }

    @Override
    public long knockdown(long volume) {
        assert(getSurplus()>=volume);
        this.completed += volume;
        return getSurplus();
    }
}
