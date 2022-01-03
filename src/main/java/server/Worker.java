package server;

import org.apache.commons.lang3.StringUtils;
import security.RSA;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Worker extends Thread {
    private final Server server;
    private final Socket socket;
    private final RSA rsa = new RSA();

    private String nickname;

    private boolean receivedClientKey;

    private InputStream inputStream;
    private OutputStream outputStream;

    public Worker(Server server, Socket socket) {
        this.nickname = null;

        this.server = server;
        this.socket = socket;

        this.receivedClientKey = false;

        init();
    }

    private void init() {
        try {

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        Thread read = new Thread(this::readCommandsLoop);
        read.start();

        sendPublicKey();
    }

    public String getNickname() {
        return nickname;
    }

    public int getPort() {
        return socket.getPort();
    }

    public String getIP() {
        String ip = "";
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        InetAddress inetAddress = inetSocketAddress.getAddress();
        if (inetAddress instanceof Inet4Address) {
            Inet4Address inet4Address = (Inet4Address) inetAddress;
            ip = inet4Address.toString();
        } else if (inetAddress instanceof Inet6Address) {
            Inet6Address inet6Address = (Inet6Address) inetAddress;
            ip = inet6Address.toString();
        } else {
            System.err.println("error - not an IP address.");
        }
        return ip.substring(1);
    }

    private void readCommandsLoop() {
        String line, response;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            OUTER:
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("key")) {
                    String[] tokens = StringUtils.split(line);
                    receiveExternalPublicKey(tokens);
                } else {
                    System.out.println("Encrypted message received: " + line);
                    line = rsa.decrypt(line);
                    System.out.println("Decrypted message: " + line + "\n");
                    String[] tokens = StringUtils.split(line);
                    if (tokens != null && tokens.length > 0) {
                        String command = tokens[0];
                        switch (command) {
                            case "login":
                                receiveLoginAttempt(tokens);
                                break;
                            case "bc":
                                tokens = StringUtils.split(line, null, 2);
                                forwardBroadcast(tokens);
                                break;
                            case "dm":
                                tokens = StringUtils.split(line, null, 3);
                                forwardDirectMessage(tokens);
                                break;
                            case "search":
                                forwardSearch(tokens);
                                break;
                            case "found":
                                forwardFoundFile(tokens);
                                break;
                            case "request":
                                forwardDownloadRequest(tokens);
                                break;
                            case "accept":
                                forwardTransferAccept(tokens);
                                break;
                            case "send":
                                forwardSendSignal(tokens);
                                break;
                            case "finished":
                                forwardTransferCompleted(tokens);
                                break;
                            case "BusySending":
                            case "KeyMismatch":
                            case "FileNotFound":
                                handleErrors(tokens);
                                break;
                            case "error":
                                System.err.println(line + "\n");
                                break;
                            case "quit":
                                break OUTER;
                            default:
                                response = "error - unknown command: " + command;
                                write(response);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        quit();
    }

    private void receiveExternalPublicKey(String[] tokens) {
        if (!receivedClientKey) {

            rsa.initExtPublicKey(tokens[1]);
            rsa.printKeys("Client");

            receivedClientKey = true;

        } else {
            String response = "error - already received key.";
            write(response);
        }
    }

    private void sendPublicKey() {
        try {

            String response = "key " + rsa.getPublicKeyString() + "\n";
            outputStream.write(response.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveLoginAttempt(String[] tokens) {
        String response;
        Worker worker = server.findWorker(tokens[1]);

        if (worker != null) {
            // unsuccessful login - nickname is already taken by another user
            response = "error - nickname already taken.";
            write(response);
        } else {

            nickname = tokens[1];

            response = "login";
            write(response);

            // receive online status of other users
            ArrayList<Worker> workerList = server.getWorkers();
            for (Worker w : workerList) {
                if (!w.equals(this) && (w.getNickname() != null)) {
                    response = "online " + w.getNickname();
                    write(response);
                }
            }

            // alert other users that current user has joined the chat
            response = "online " + nickname;
            workerList = server.getWorkers();
            for (Worker w : workerList) {
                if (!w.equals(this) && (w.getNickname() != null)) {
                    w.write(response);
                }
            }
        }
    }

    private void forwardBroadcast(String[] tokens) {
        ArrayList<Worker> workerList;
        String response = "bc " + nickname + ": " + tokens[1];

        // broadcast message to all users
        workerList = server.getWorkers();
        for (Worker worker : workerList) {
            if (!worker.equals(this)) {
                worker.write(response);
            }
        }
    }

    private void forwardDirectMessage(String[] tokens) {
        String response = "dm " + nickname + " " + tokens[2];
        Worker worker = server.findWorker(tokens[1]);

        if (worker != null) {
            worker.write(response);
        } else {
            response = "error - peer not found.";
            write(response);
        }
    }

    private void forwardSearch(String[] tokens) {
        ArrayList<Worker> workers = server.getWorkers();

        for (Worker worker : workers) {
            if ((worker.getNickname() != null) && (worker != this)) {
                String response = "search " + tokens[1] + " " + tokens[2];
                worker.write(response);
            }
        }
    }

    private void forwardFoundFile(String[] tokens) {
        String response;
        Worker worker = server.findWorker(tokens[3]);

        if (worker != null) {
            response = "found " + tokens[1] + " " + tokens[2];
            worker.write(response);
        } else {
            response = "error - peer not found.";
            write(response);
        }
    }

    private void forwardDownloadRequest(String[] tokens) {
        String response;
        Worker worker = server.findWorker(tokens[3]);

        if (worker != null) {
            response = "request " + tokens[1] + " " + tokens[2] + " " + nickname;
            worker.write(response);
        } else {
            response = "error - peer not found.";
            write(response);
        }
    }

    private void forwardTransferAccept(String[] tokens) {
        String response;
        Worker worker = server.findWorker(tokens[4]);

        if (worker == null) {
            // error finding recipient nickname
            response = "error - peer not found.";
            write(response);
        } else {
            response = "accept " + tokens[1] + " " + tokens[2] + " " + tokens[3] + " " + nickname;
            worker.write(response);
        }
    }

    private void forwardSendSignal(String[] tokens) {
        String response;
        Worker worker = server.findWorker(tokens[1]);

        if (worker == null) {
            // error finding recipient nickname
            response = "error - peer not found.";
            write(response);
        } else {
            response = "send " + getIP() + " " + getPort();
            worker.write(response);
        }
    }

    private void forwardTransferCompleted(String[] tokens) {
        String message;
        Worker worker = server.findWorker(tokens[1]);

        if (worker == null) {
            // error finding recipient nickname
            message = "error - peer not found.";
            write(message);
        } else {
            message = "finished";
            worker.write(message);
        }
    }

    private void handleErrors(String[] tokens) {
        String response;
        Worker worker = server.findWorker(tokens[1]);

        if (worker != null) {
            switch (tokens[0]) {
                case "KeyMismatch":
                    response = "error - keys did not match.";
                    break;
                case "FileNotFound":
                    response = "error - file not found.";
                    break;
                default:
                    response = "error - peer busy sending.";
                    break;
            }
            worker.write(response);
        } else {
            response = "error - peer not found.";
            write(response);
        }
    }

    private void quit() {
        String response;
        ArrayList<Worker> workerList;

        // alert other users that current user has left the chat
        if (nickname != null) {
            response = "offline " + nickname;
            workerList = server.getWorkers();
            for (Worker worker : workerList) {
                if (!worker.equals(this) && worker.getNickname() != null) {
                    worker.write(response);
                }
            }
        }

        // remove current user from list of users
        server.removeWorker(this);

        // close socket connection
        System.out.println("disconnect ... " + socket + "\n");

    }

    public void write(String response) {
        try {
            System.out.println("Response: " + response);
            String encryptedMessage = rsa.encrypt(response) + "\n";
            System.out.print("Encrypted response sent: " + encryptedMessage + "\n");
            outputStream.write(encryptedMessage.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
