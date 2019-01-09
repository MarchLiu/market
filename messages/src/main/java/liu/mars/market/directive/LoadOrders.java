package liu.mars.market.directive;

public class LoadOrders {
    private long from;
    private int limit;

    public LoadOrders(long from, int limit) {
        this.from = from;
        this.limit = limit;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
