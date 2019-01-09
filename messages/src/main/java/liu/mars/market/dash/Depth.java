package liu.mars.market.dash;

import java.time.LocalDateTime;
import java.util.List;

public class Depth {
    private List<Level> ask;
    private List<Level> bid;
    private LocalDateTime ts;
    private String channel;
    private long version;

    public List<Level> getAsk() {
        return ask;
    }

    public void setAsk(List<Level> ask) {
        this.ask = ask;
    }

    public List<Level> getBid() {
        return bid;
    }

    public void setBid(List<Level> bid) {
        this.bid = bid;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
