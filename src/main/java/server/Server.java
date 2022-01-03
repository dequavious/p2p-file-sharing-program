package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private final int port;

    private final ArrayList<Worker> workers = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {

            // open a ServerSocket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("started server ...\n");

            //noinspection InfiniteLoopStatement
            while (true) {
                // accept router/client connection
                Socket socket = serverSocket.accept();

                // print connection
                System.out.println("established connection ... " + socket + "\n");

                Worker worker = new Worker(this, socket);
                worker.start();

                workers.add(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    public Worker findWorker(String nickname) {
        for (Worker worker : workers) {
            if (nickname.equals(worker.getNickname())) {
                return worker;
            }
        }
        return null;
    }

    public void removeWorker(Worker worker) {
        workers.remove(worker);
    }
}