package client.util;

public class SharedFile {
    private final double fileSize;
    private final String path;

    public SharedFile(double fileSize, String path) {
        this.fileSize = fileSize;
        this.path = path;
    }

    public String getSizeAsString() {
        return String.valueOf(fileSize);
    }

    public double getFileSize() {
        return fileSize;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "SharedFile{" +
                "fileSize=" + fileSize +
                ", path='" + path + '\'' +
                '}';
    }
}
