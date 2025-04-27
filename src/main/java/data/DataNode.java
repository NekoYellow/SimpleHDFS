package data;

import java.io.*;

public class DataNode implements Serializable {
    private final String nodeId;

    public DataNode(String nodeId) {
        this.nodeId = nodeId;
        File dir = new File(nodeId);
        if (!dir.exists()) dir.mkdirs();
    }

    public void writeBlock(DataBlock block) {
        String fileName = nodeId + "/" + block.blockId + ".txt";
        File file = new File(fileName);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(block.data);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public DataBlock readBlock(String blockId) {
        String fileName = nodeId + "/" + blockId + ".txt";
        File file = new File(fileName);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        } catch (IOException e) {
//            e.printStackTrace();
            return null;
        }
        return new DataBlock(blockId, data);
    }

    public String getNodeId() {
        return nodeId;
    }
}
