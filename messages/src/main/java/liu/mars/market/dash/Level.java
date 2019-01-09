package liu.mars.market.dash;

import java.math.BigDecimal;
import java.util.List;

public class Level {
    private BigDecimal price;
    private long quantity;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public static Level from(Make order){
        var result = new Level();
        result.setPrice(order.getPrice());
        result.setQuantity(order.getSurplus());
        return result;
    }

    public static Level accumulate(List<Make> orders) {
        var result = new Level();
        result.setPrice(orders.get(0).getPrice());
        orders.forEach(item -> result.quantity+=item.getSurplus());
        return result;
    }
}
