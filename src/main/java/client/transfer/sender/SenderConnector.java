package client.transfer.sender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class SenderConnector {
    private final String serverName;
    private final int port;

    public SenderConnector(String serverName, int port) {
        this.serverName = serverName;
        this.port = port + 100;
    }

    public SocketChannel CreateChannel() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(serverName, port);
        socketChannel.connect(socketAddress);
        return socketChannel;
    }
}