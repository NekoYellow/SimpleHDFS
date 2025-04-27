package name;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileMetadata implements Serializable {
    public String fileName;
    public int fileSize;
    public List<String> blockIds;

    public FileMetadata(String fileName, int fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.blockIds = new ArrayList<>();
    }

    public FileMetadata(String fileName, int fileSize, List<String> blockIds) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.blockIds = new ArrayList<>(blockIds);
    }
}
