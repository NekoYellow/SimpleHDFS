package cli;

import java.util.*;
import java.util.random.RandomGenerator;

import data.DataBlock;
import data.DataNode;
import name.FileMetadata;
import name.NameNode;

public class Client {
    NameNode nameNode;
    List<DataNode> dataNodes;
    Map<String, Boolean> openFiles;

    public Client(int numDataNodes) {
        this.nameNode = NameNode.load();

        this.dataNodes = new ArrayList<>();
        for (int i = 1; i <= numDataNodes; i++) {
            dataNodes.add(new DataNode("node" + i));
        }

        this.openFiles = new HashMap<>();
    }

    public void open(String fileName) {
        if (openFiles.containsKey(fileName)) {
            System.out.println("File is already open.");
            return;
        }

        FileMetadata meta = nameNode.getFileMetadata(fileName);
        if (meta == null) {
            nameNode.createFile(fileName);
        }

        openFiles.put(fileName, true);
        System.out.println("File " + fileName + " opened.");
    }

    private void allocateBlocks(String fileName) {
        int r = NameNode.replicaCount;
        int i = RandomGenerator.getDefault().nextInt(dataNodes.size());
        for (int j = 0; j < r; j++) {
            nameNode.registerBlock(fileName, dataNodes.get((i+j) % dataNodes.size()));
        }
    }

    public void write(String fileName, byte[] data) {
        if (openFiles.get(fileName) == null) {
            System.out.println("File is not open.");
            return;
        }

        FileMetadata meta = nameNode.getFileMetadata(fileName);
        int length = data.length, b = NameNode.blockSize, r = NameNode.replicaCount, offset = meta.fileSize;

        for (int i = 0; i < length; ) {
            int inc = Math.min(length - i, b - (offset + i) % b);

            int blockIndex = (offset + i) / b;
            if (blockIndex * r >= meta.blockIds.size()) {
                allocateBlocks(fileName);
            }

            for (int j = 0; j < r; j++) {
                String blockId = meta.blockIds.get(blockIndex * r + j);
                DataNode dataNode = nameNode.whichNode(blockId);
                DataBlock block = dataNode.readBlock(blockId);
                if (block == null) block = new DataBlock(blockId, new byte[b]);
                System.arraycopy(data, i, block.data, (offset + i) % b, inc);
                dataNode.writeBlock(block);
                System.out.println("Written data to " + blockId);
            }

            i += inc;
        }

        meta.fileSize += data.length;
    }

    public byte[] read(String fileName, int length) {
        if (openFiles.get(fileName) == null) {
            System.out.println("File is not open.");
            return null;
        }

        FileMetadata meta = nameNode.getFileMetadata(fileName);
        int b = NameNode.blockSize, r = NameNode.replicaCount;

        if (length == -1) length = meta.fileSize;
        byte[] data = new byte[length];

        for (int i = 0; i < length; ) {
            int inc = Math.min(length - i, b);

            int blockIndex = i / b;

            String blockId = meta.blockIds.get(blockIndex * r);
            DataNode dataNode = nameNode.whichNode(blockId);
            DataBlock block = dataNode.readBlock(blockId);
            System.arraycopy(block.data, 0, data, i, inc);
            System.out.println("Data read from " + blockId);

//            handle.offset += inc;
            i += inc;
        }

//        handle.offset += length;

        return data;
    }

    public void close(String fileName) {
        if (!openFiles.containsKey(fileName)) {
            System.out.println("File is not open.");
            return;
        }

        openFiles.remove(fileName);
        System.out.println("File " + fileName + " closed.");
    }

    public void exit() {
        nameNode.persist();
    }
}
