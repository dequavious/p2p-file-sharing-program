package server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server(2620);
        server.start();
    }
}
