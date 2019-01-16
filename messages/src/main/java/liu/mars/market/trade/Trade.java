package liu.mars.market.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    private long id;
    private long takerId;
    private String symbol;
    private String takerCategory;
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

    @JsonProperty("taker-id")
    public long getTakerId() {
        return takerId;
    }

    public void setTakerId(long takerId) {
        this.takerId = takerId;
    }

    @JsonProperty("taker-category")
    public String getTakerCategory() {
        return takerCategory;
    }

    public void setTakerCategory(String takerCategory) {
        this.takerCategory = takerCategory;
    }

    @JsonProperty("trade-items")
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