package liu.mars.market.dash;

import clojure.java.api.Clojure;
import clojure.lang.APersistentMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liu.mars.market.messages.Limit;
import liu.mars.market.messages.TradeOrder;
import liu.mars.market.trade.TradeItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public abstract class Make {
    private static ObjectMapper mapper = new ObjectMapper();
    private long orderId;
    private BigDecimal price;
    private long quantity;
    private long completed;
    private String symbol;

    protected Object keyword(String name){
        return Clojure.read(String.format(":%s", name));
    }

    protected Object get(APersistentMap map, String keyName){
        return map.get(keyword(keyName));
    }

    protected Object get(APersistentMap map, String keyName, Object defaultValue){
        return map.getOrDefault(keyword(keyName), defaultValue);
    }

    protected Make(){}

    @JsonProperty("price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonSetter("price")
    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @JsonIgnore
    public BigDecimal getAmount() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("order-id")
    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @JsonProperty(defaultValue = "0")
    public long getCompleted() {
        return completed;
    }

    @JsonIgnore
    public long getSurplus() {
        return quantity - completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    protected Optional<TradeItem> trade(TradeOrder order) {
        if (order.getSurplus() == 0 || getSurplus() == 0) {
            return Optional.empty();
        }

        TradeItem item = new TradeItem();
        item.setPrice(this.getPrice());
        item.setMakerId(getOrderId());
        long turnover = Math.min(order.getSurplus(), getSurplus());
        item.setTurnover(turnover);
        order.knockdown(turnover);
        this.completed += turnover;
        return Optional.of(item);
    }

    protected Make(Limit order) {
        this.setOrderId(order.getId());
        this.setCompleted(order.getCompleted());
        this.setPrice(order.getPrice());
        this.setQuantity(order.getQuantity());
        this.setSymbol(order.getSymbol());
    }

    protected Make(APersistentMap map) {
        this.setOrderId((Long)get(map, "order-id"));
        this.setCompleted((Long)get(map, "completed", 0L));
        this.setPrice(new BigDecimal(get(map , "price").toString()));
        this.setQuantity((Long)get(map, "quantity"));
        this.setSymbol(get(map, "symbol").toString());
    }

    protected Make(JsonNode json) {
        this.setOrderId(json.get("order-id").longValue());
        this.setCompleted(json.get("completed").asLong(0L));
        this.setPrice(new BigDecimal(json.get("price").asText()));
        this.setQuantity(json.get("quantity").asLong());
        this.setSymbol(json.get("symbol").asText());
    }

    protected Make(String json) throws IOException {
        this(mapper.readTree(json));
    }
}
