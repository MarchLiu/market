package liu.mars.market.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import liu.mars.market.dash.Ask;
import liu.mars.market.dash.Bid;

import java.util.ArrayList;
import java.util.List;

public class DashStatus {
    private long id;
    private long latestOrderId;
    private String symbol;
    private List<Ask> askList=new ArrayList<>();
    private List<Bid> bidList=new ArrayList<>();
    private String status;

    @JsonProperty
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("latest-order-id")
    public long getLatestOrderId() {
        return latestOrderId;
    }

    public void setLatestOrderId(long latestOrderId) {
        this.latestOrderId = latestOrderId;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("asks")
    public List<Ask> getAskList() {
        return askList;
    }

    public void setAskList(List<Ask> askList) {
        this.askList = askList;
    }

    @JsonProperty("bids")
    public List<Bid> getBidList() {
        return bidList;
    }

    public void setBidList(List<Bid> bidList) {
        this.bidList = bidList;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
