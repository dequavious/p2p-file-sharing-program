package client.transfer.sender;

import client.gui.UploadFrame;
import client.util.SharedFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TCPSender extends Thread {
    private final SocketChannel socketChannel;
    private final SharedFile file;
    private final UploadFrame uploadFrame;

    private int percentage;

    public TCPSender(SocketChannel socketChannel, SharedFile file, UploadFrame uploadFrame) {
        this.socketChannel = socketChannel;
        this.file = file;
        this.uploadFrame = uploadFrame;

        this.percentage = 0;
    }

    @Override
    public void run() {
        try {

            System.out.println("Sending file via TCP ...\n");

            // START TIMER
            long startTime = System.currentTimeMillis();

            sendFile();

            // TIME IT TOOK FOR FILE TRANSMISSION TO COMPLETE
            long timeElapsed = System.currentTimeMillis() - startTime;

            Thread.sleep(100);

            System.out.println("File has been sent");

            // PRINT OUT ELAPSED TIME
            System.out.println("Time elapsed: " + (timeElapsed / 1000.0) + "s\n");

            socketChannel.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendFile() throws IOException {
        int percentageCompleted;
        int nrBytesSentSoFar = 0;
        double fileSize = file.getFileSize();
        Path path = Paths.get(file.getPath());

        FileChannel fileChannel = FileChannel.open(path);
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        int bytesRead = fileChannel.read(buffer);

        Thread update = new Thread(this::updatePercentage);
        update.start();

        while (bytesRead != -1) {
            buffer.flip();
            socketChannel.write(buffer);

            nrBytesSentSoFar = nrBytesSentSoFar + bytesRead;
            percentageCompleted = (int) ((nrBytesSentSoFar / fileSize) * 100);

            if (percentageCompleted > percentage) {
                percentage = percentageCompleted;
                update = new Thread(this::updatePercentage);
                update.start();
            }

            buffer.compact();
            bytesRead = fileChannel.read(buffer);
        }
    }

    private void updatePercentage() {
        uploadFrame.setProgress(percentage);
    }
}
