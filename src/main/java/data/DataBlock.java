package data;

import java.io.Serializable;

public class DataBlock implements Serializable {
    public String blockId;
    public byte[] data;

    public DataBlock(String blockId, byte[] data) {
        this.blockId = blockId;
        this.data = data;
    }
}
