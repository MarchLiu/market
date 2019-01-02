package liu.mars.market.messages;

public interface Order {
    long getId();
    void setId(long id);
    long getAccountId();
    String getSymbol();
}
