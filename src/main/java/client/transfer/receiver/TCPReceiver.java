package client.transfer.receiver;

import client.Client;
import client.gui.DownloadFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class TCPReceiver extends Thread {
    private final Client client;
    private final String sender;
    private final String filename;
    private final SocketChannel socketChannel;
    private final Double fileSize;
    private final DownloadFrame downloadFrame;

    private int percentage;

    public TCPReceiver(Client client, String sender, SocketChannel socketChannel, String filename, Double fileSize, DownloadFrame downloadFrame) {
        this.client = client;
        this.sender = sender;
        this.socketChannel = socketChannel;
        this.filename = filename;
        this.fileSize = fileSize;
        this.downloadFrame = downloadFrame;

        this.percentage = 0;
    }

    @Override
    public void run() {
        try {
            System.out.println("Receiving file via TCP ...\n");

            // START TIMER
            long startTime = System.currentTimeMillis();

            // START RECEIVING FILE
            readFileFromSocketChannel();

            // TIME IT TOOK FOR FILE TRANSMISSION TO COMPLETE
            long timeElapsed = System.currentTimeMillis() - startTime;

            Thread.sleep(100);

            System.out.println("TCP file transfer complete");

            // PRINT OUT ELAPSED TIME
            System.out.println("Time elapsed: " + (timeElapsed / 1000.0) + "s\n");

            socketChannel.close();

            String response = "finished " + sender;
            client.writeCommand(response);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readFileFromSocketChannel() throws IOException {
        int percentageCompleted;
        int nrBytesReadSoFar = 0;
        String destination = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "downloads" + File.separator + filename;

        File file = new File(destination);

        ByteBuffer buffer = ByteBuffer.allocate(2048);
        int bytesRead = socketChannel.read(buffer);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel fileChannel = fileOutputStream.getChannel();

        Thread update = new Thread(this::updatePercentage);
        update.start();

        while (bytesRead != -1) {
            buffer.flip();
            fileChannel.write(buffer);

            nrBytesReadSoFar = nrBytesReadSoFar + bytesRead;
            percentageCompleted = (int) ((nrBytesReadSoFar / fileSize) * 100);

            if (percentageCompleted > percentage) {
                percentage = percentageCompleted;
                update = new Thread(this::updatePercentage);
                update.start();
            }

            buffer.compact();
            bytesRead = socketChannel.read(buffer);
        }
    }

    private void updatePercentage() {
        downloadFrame.setProgress(percentage);
    }
}
