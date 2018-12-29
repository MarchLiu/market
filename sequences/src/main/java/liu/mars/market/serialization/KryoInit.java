package liu.mars.market.serialization;

import com.esotericsoftware.kryo.Kryo;
import liu.mars.market.messages.CreateSequence;
import liu.mars.market.messages.DropSequence;
import liu.mars.market.messages.ListSequences;
import liu.mars.market.messages.NextValue;

public class KryoInit {
    public void customize(Kryo kryo){
        kryo.register(CreateSequence.class);
        kryo.register(DropSequence.class);
        kryo.register(ListSequences.class);
        kryo.register(NextValue.class);
    }
}
