package client.transfer.receiver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ReceiverConnector {
    private final int port;

    private ServerSocketChannel serverSocket;

    public ReceiverConnector(int port) {
        this.port = port + 100;
    }

    public SocketChannel getClientSocketChannel() throws IOException {
        return serverSocket.accept();
    }

    public ServerSocketChannel createServerSocketChannel() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));

        return serverSocket;
    }
}
