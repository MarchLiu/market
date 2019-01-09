package liu.mars.market.trade;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    private long id;
    private long takerId;
    private String symbol;
    private String takeDirection;
    private List<TradeItem> tradeItems = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTakerId() {
        return takerId;
    }

    public void setTakerId(long takerId) {
        this.takerId = takerId;
    }

    public String getTakeDirection() {
        return takeDirection;
    }

    public void setTakeDirection(String takeDirection) {
        this.takeDirection = takeDirection;
    }

    public List<TradeItem> getTradeItems() {
        return tradeItems;
    }

    public void setTradeItems(List<TradeItem> tradeItems) {
        this.tradeItems = tradeItems;
    }

    public long getTurnover() {
        return tradeItems.stream().mapToLong(TradeItem::getTurnover).sum();
    }

    public void add(TradeItem item){
        this.tradeItems.add(item);
    }
}