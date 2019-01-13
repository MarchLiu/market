package liu.mars.market;

import akka.serialization.JSerializer;
import clojure.lang.IFn;
import clojure.lang.RT;

public class NippySerializer extends JSerializer {
    private static IFn require = RT.var("clojure.core", "require").fn();
    private static IFn freeze;
    private static IFn thaw;
    static {
        require.invoke(RT.readString("taoensso.nippy"));
        freeze = RT.var("taoensso.nippy", "freeze");
        thaw = RT.var("taoensso.nippy", "thaw");
    }

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        return thaw.invoke(bytes);
    }

    @Override
    public int identifier() {
        return 12798007;
    }

    @Override
    public byte[] toBinary(Object o) {
       return (byte[])freeze.invoke(o);
    }

    @Override
    public boolean includeManifest() {
        return false;
    }


}
