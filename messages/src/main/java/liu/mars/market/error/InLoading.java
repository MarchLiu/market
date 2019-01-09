package liu.mars.market.error;

public class InLoading extends Exception {
    public InLoading(String symbol){
        super(String.format("dash %s in loading.", symbol));
    }
}
