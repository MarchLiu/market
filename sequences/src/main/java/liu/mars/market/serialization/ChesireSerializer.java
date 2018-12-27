package liu.mars.market.serialization;

import akka.serialization.JSerializer;
import clojure.lang.IFn;
import clojure.lang.RT;

import java.util.Arrays;

public class ChesireSerializer extends JSerializer {
    private static IFn require = RT.var("clojure.core", "require").fn();
    private static IFn generate;
    private static IFn parse;
    static {
        require.invoke(RT.readString("cheshire.core"));
        generate = RT.var("cheshire.core", "generate-string");
        parse = RT.var("cheshire.core", "parse-string");
    }

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        return parse.invoke(Arrays.toString(bytes));
    }

    @Override
    public int identifier() {
        return 1279807;
    }

    @Override
    public byte[] toBinary(Object o) {
        return ((String)generate.invoke(o)).getBytes();
    }

    @Override
    public boolean includeManifest() {
        return false;
    }


}
