package liu.mars.market.messages;

public class MarketBid implements TradeOrder {
    private long id;
    private long quantity;
    private long accountId;
    private long completed;
    private String symbol;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
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
