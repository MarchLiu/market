package liu.mars.market.messages;

public interface TradeOrder extends Order {
    long getQuantity();
    long getCompleted();
    long getSurplus();
    long knockdown(long volume);
}
