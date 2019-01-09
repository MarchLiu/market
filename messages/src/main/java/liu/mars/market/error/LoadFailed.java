package liu.mars.market.error;

public class LoadFailed extends Exception{
    public LoadFailed(String symbol, String message){
        super(String.format("dash %s load failed: %s.", symbol, message));
    }
}
