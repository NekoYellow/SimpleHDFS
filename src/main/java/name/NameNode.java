package name;

import data.DataNode;

import java.io.*;
import java.util.*;

public class NameNode implements Serializable {
    private Map<String, FileMetadata> fileTable; // fileName -> metadata
    private Map<String, DataNode> blockTable; // blockId -> dataNode
    public static final int replicaCount = 3;
    private int blockCounter;

    public NameNode() {
        this.fileTable = new HashMap<>();
        this.blockTable = new HashMap<>();
        this.blockCounter = 0;
    }

    public void createFile(String fileName) {
        if (fileTable.containsKey(fileName))
            return;
        fileTable.put(fileName, new FileMetadata(fileName, 0));
    }

    public void registerBlock(String fileName, DataNode node) {
        String blockId = fileName + "_block_" + (blockCounter++);
        fileTable.get(fileName).blockIds.add(blockId);
        blockTable.put(blockId, node);
    }

    public DataNode whichNode(String blockId) {
        return blockTable.get(blockId);
    }

    public FileMetadata getFileMetadata(String fileName) {
        return fileTable.get(fileName);
    }

    public void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("fsimage"))) {
            System.out.println("Persisting to fs image");
            oos.writeObject(this);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public static NameNode load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("fsimage"))) {
            System.out.println("Loading from fs image");
            return (NameNode) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
            return new NameNode();
        }
    }
}

