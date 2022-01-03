package client;

import client.gui.*;
import client.handler.OnExit;
import client.listener.MessageListener;
import client.listener.StatusListener;
import client.transfer.receiver.ReceiverConnector;
import client.transfer.receiver.TCPReceiver;
import client.transfer.sender.SenderConnector;
import client.transfer.sender.TCPSender;
import client.util.SharedFile;
import org.apache.commons.lang3.StringUtils;
import security.AES;
import security.RSA;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class Client extends Thread {
    private final Socket socket;
    private final HashMap<String, SharedFile> sharedFiles = new HashMap<>();
    private final HashMap<String, ChatWindowFrame> chatWindows = new HashMap<>();
    private final HashMap<String, ArrayList<String>> messageHistory = new HashMap<>();
    private final HashMap<String, MessageListener> messageListeners = new HashMap<>();

    private final ArrayList<String> onlineUsers = new ArrayList<>();
    private final ArrayList<String> broadcasts = new ArrayList<>();

    private final RSA rsa = new RSA();
    private final AES aes = new AES(generateSecretKey());

    private boolean sending;
    private boolean loggedIn;
    private boolean receiving;
    private boolean receivedServerKey;

    private String key;
    private String nickname;
    private String sendingPeer;
    private String receivingPeer;
    private SharedFile sharedFile;
    private LoginFrame loginFrame;
    private ChatRoomFrame chatFrame;
    private UploadFrame uploadFrame;
    private TransferFrame clientFrame;
    private DownloadFrame downloadFrame;
    private InputStream inputStream;
    private OutputStream outputStream;
    private StatusListener statusListener;
    private MessageListener broadcastListener;
    private SocketChannel sendSocketChannel;
    private SocketChannel receiveSocketChannel;
    private ServerSocketChannel serverSocketChannel;

    public Client(Socket socket) {
        this.socket = socket;

        this.sending = false;
        this.loggedIn = false;
        this.receiving = false;
        this.receivedServerKey = false;

        this.key = null;
        this.nickname = null;
        this.sharedFile = null;
        this.sendingPeer = null;
        this.receivingPeer = null;
        this.statusListener = null;
        this.broadcastListener = null;
        this.sendSocketChannel = null;
        this.serverSocketChannel = null;
        this.receiveSocketChannel = null;

        Runtime.getRuntime().addShutdownHook(new OnExit(this));

        init();
    }

    private void init() {
        try {

            //noinspection ResultOfMethodCallIgnored
            new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "downloads").mkdirs();

            chatFrame = new ChatRoomFrame(this, onlineUsers.toArray(new String[0]), broadcasts.toArray(new String[0]));
            loginFrame = new LoginFrame(this);
            uploadFrame = new UploadFrame();
            downloadFrame = new DownloadFrame();
            clientFrame = new TransferFrame(this);

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        java.awt.EventQueue.invokeLater(() -> loginFrame.setVisible(true));

        Thread read = new Thread(this::readResponsesLoop);
        read.start();

        sendPublicKey();
    }

    public int getPort() {
        return socket.getLocalPort();
    }

    private void readResponsesLoop() {
        try {
            String line;
            String[] tokens;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("key")) {
                    tokens = StringUtils.split(line);
                    receiveExternalPublicKey(tokens);
                } else {
                    System.out.println("Encrypted response received: " + line);
                    line = rsa.decrypt(line);
                    System.out.println("Decrypted response: " + line + "\n");
                    tokens = StringUtils.split(line);
                    if (tokens != null && tokens.length > 0) {
                        switch (tokens[0]) {
                            case "login":
                                login();
                                break;
                            case "online":
                                receiveOnlineStatus(tokens);
                                break;
                            case "offline":
                                receiveOfflineStatus(tokens);
                                break;
                            case "bc":
                                receiveBroadcast(line);
                                break;
                            case "dm":
                                receiveDirectMessage(tokens, line);
                                break;
                            case "search":
                                findCloseFiles(tokens);
                                break;
                            case "request":
                                receiveDownloadRequest(tokens);
                                break;
                            case "finished":
                                finishedSending();
                                break;
                            case "send":
                                sendFile(tokens);
                                break;
                            case "accept":
                                downloadFile(tokens);
                                break;
                            case "found":
                                clientFrame.addToList(tokens);
                                break;
                            case "error":
                                handleErrors(line);
                                break;
                            default:
                                System.out.println(line);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeCommand(String command) {
        try {
            String[] tokens = StringUtils.split(command);
            if (tokens != null && tokens.length > 0) {
                switch (tokens[0]) {
                    case "login":
                        sendLoginAttempt(tokens);
                        break;
                    case "bc":
                        tokens = StringUtils.split(command, null, 2);
                        sendBroadcast(tokens);
                        break;
                    case "dm":
                        tokens = StringUtils.split(command, null, 3);
                        sendDirectMessage(tokens);
                        break;
                    case "share":
                        tokens = StringUtils.split(command, null, 2);
                        addSharedFile(tokens);
                        break;
                    case "remove":
                        removeSharedFile(tokens);
                        break;
                    case "search":
                        tokens = StringUtils.split(command, null, 2);
                        searchFile(tokens);
                        break;
                    case "request":
                        sendDownloadRequest(tokens);
                        break;
                    case "finished":
                        finishedDownloading(command);
                        break;
                    case "quit":
                        write("quit");
                        break;
                    default:
                        write(command);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void receiveExternalPublicKey(String[] tokens) {
        if (!receivedServerKey) {

            rsa.initExtPublicKey(tokens[1]);
            rsa.printKeys("Server");

            receivedServerKey = true;

        } else {
            String response = "error - already received key.";
            write(response);
        }
    }

    private void sendLoginAttempt(String[] tokens) {
        if (!loggedIn) {
            if (tokens.length == 2) {
                nickname = tokens[1];
                String command = tokens[0] + " " + tokens[1];
                write(command);
            } else if (tokens.length > 2) {
                System.err.println("error - nickname may only be one word.");
                loginFrame.throwError("nickname may only be one word");
            } else {
                System.err.println("error - no nickname provided.");
            }
        } else {
            System.err.println("error - already logged in.");
        }
    }

    private void login() {
        loginFrame.setVisible(false);
        java.awt.EventQueue.invokeLater(() -> clientFrame.setVisible(true));
        loggedIn = true;
    }

    private void receiveOnlineStatus(String[] tokens) {
        onlineUsers.add(tokens[1]);
        if (statusListener != null) {
            handleOnlineStatus(tokens);
        }
    }

    private void receiveOfflineStatus(String[] tokens) {
        onlineUsers.remove(tokens[1]);
        if (statusListener != null) {
            handleOfflineStatus(tokens);
        }
        messageHistory.remove(tokens[1]);
        messageListeners.remove(tokens[1]);
    }

    private void sendBroadcast(String[] tokens) {
        if (loggedIn) {
            if (tokens.length == 2) {
                String command = tokens[0] + " " + tokens[1];
                write(command);
                broadcasts.add("You: " + tokens[1]);
            } else {
                System.err.println("error - too few/many arguments.");
            }
        } else {
            System.err.println("error - login required.");
        }
    }

    private void sendDirectMessage(String[] tokens) {
        if (loggedIn) {
            if (tokens.length == 3) {
                String command = tokens[0] + " " + tokens[1] + " " + tokens[2];
                write(command);
                ArrayList<String> messages = messageHistory.get(tokens[1]);
                if (messages != null) {
                    messages.add("You: " + tokens[2]);
                }
            } else {
                System.err.println("error - too few/many arguments.");
            }
        } else {
            System.err.println("error - login required.");
        }
    }

    private void receiveDirectMessage(String[] tokens, String line) {
        ArrayList<String> messages;
        String message = line.replace("dm " + tokens[1] + " ", tokens[1] + ": ");

        if ((messages = messageHistory.get(tokens[1])) == null) {
            messages = new ArrayList<>();
            messageHistory.put(tokens[1], messages);
        }
        messages.add(message);

        ChatWindowFrame chatWindow = chatWindows.get(tokens[1]);

        if (chatWindow == null) {

            chatWindow = new ChatWindowFrame(this, tokens[1], messages.toArray(new String[0]));
            chatWindow.setVisible(true);
            chatWindow.setFocusable(true);

            chatWindows.put(tokens[1], chatWindow);

        } else if (!chatWindow.isFocusable()) {
            chatWindow.dispose();

            chatWindow = new ChatWindowFrame(this, tokens[1], messages.toArray(new String[0]));
            chatWindow.setVisible(true);

            chatWindows.replace(tokens[1], chatWindow);

        } else {
            messageListeners.putIfAbsent(tokens[1], chatWindow);
            messageListeners.get(tokens[1]).message(message);
        }
    }

    private void receiveBroadcast(String line) {
        String message = line.replace("bc ", "");
        if (broadcastListener != null) {
            broadcastListener.message(message);
        }
        broadcasts.add(message);
    }

    private void addSharedFile(String[] tokens) {
        if (loggedIn) {
            if (tokens.length == 2) {
                File file = new File(tokens[1]);
                if (file.exists()) {
                    double filesize = file.length();
                    SharedFile newSharedFile = new SharedFile(filesize, tokens[1]);
                    String filename = tokens[1].substring(tokens[1].lastIndexOf(File.separator) + 1).replace(" ", "_");
                    if (sharedFiles.putIfAbsent(filename, newSharedFile) != null) {
                        System.err.println("error - file already shared.");
                        clientFrame.throwError("file already shared");
                    }
                } else {
                    System.err.println("error - file does not exist.");
                }
            } else {
                System.err.println("error - too few/many arguments.");
            }
        } else {
            System.err.println("error - login required.");
        }
    }

    private void removeSharedFile(String[] tokens) {
        if (loggedIn) {
            if (tokens.length == 2) {
                sharedFiles.remove(tokens[1]);
            } else {
                System.err.println("error - too few/many arguments.");
            }
        } else {
            System.err.println("error - login required.");
        }
    }

    private void searchFile(String[] tokens) {
        if (loggedIn) {
            if (tokens.length == 2) {
                String command = tokens[0] + " " + tokens[1].replace(" ", "_") + " " + nickname;
                write(command);
            } else {
                System.err.println("error - too few/many arguments.");
            }
        } else {
            System.err.println("error - login required.");
        }
    }

    private void findCloseFiles(String[] tokens) {
        sharedFiles.keySet().stream().filter(fileName -> (StringUtils.containsIgnoreCase(fileName, tokens[1]))).map(fileName -> "found " + fileName + " " + nickname + " " + tokens[2]).forEachOrdered(this::write);
    }

    private void sendDownloadRequest(String[] tokens) throws Exception {
        if (loggedIn) {
            if (!receiving) {
                if (tokens.length == 3) {

                    key = generateSecretKey();

                    System.out.println("Generated key: " + key);
                    String encryptedKey = aes.encrypt(key);
                    System.out.println("Encrypted key sent: " + encryptedKey + "\n");

                    String command = "request " + tokens[1] + " " + aes.encrypt(key) + " " + tokens[2];
                    write(command);

                } else {
                    System.err.println("error - too few/many arguments.");
                }
            } else {
                System.err.println("error - already receiving.");
                clientFrame.throwError("already receiving");
            }
        } else {
            System.err.println("error - login required.");
        }
    }

    private void receiveDownloadRequest(String[] tokens) {
        SharedFile temp;
        if (!sending) {
            if ((temp = sharedFiles.get(tokens[1])) != null) {
                sharedFile = temp;
                receivingPeer = tokens[3];
                String command = "accept " + tokens[1] + " " + sharedFile.getSizeAsString() + " " + tokens[2] + " " + receivingPeer;
                write(command);
            } else {
                String response = "FileNotFound " + tokens[3];
                write(response);
            }
        } else {
            String response = "BusySending " + tokens[3];
            write(response);
        }
    }

    private void sendFile(String[] tokens) throws IOException {

        java.awt.EventQueue.invokeLater(() -> uploadFrame.setVisible(true));

        sending = true;
        SenderConnector senderConnector = new SenderConnector(tokens[1], Integer.parseInt(tokens[2]));
        sendSocketChannel = senderConnector.CreateChannel();
        TCPSender tcpSender = new TCPSender(sendSocketChannel, sharedFile, uploadFrame);
        tcpSender.start();

    }

    private void downloadFile(String[] tokens) throws Exception {

        System.out.println("Encrypted key received: " + tokens[3]);
        String decryptedKey = aes.decrypt(tokens[3]);
        System.out.println("Decrypted key: " + decryptedKey + "\n");

        if (key.equals(decryptedKey)) {

            java.awt.EventQueue.invokeLater(() -> downloadFrame.setVisible(true));

            receiving = true;
            sendingPeer = tokens[4];
            ReceiverConnector receiverConnector = new ReceiverConnector(getPort());
            serverSocketChannel = receiverConnector.createServerSocketChannel();

            String message = "send " + sendingPeer;
            write(message);

            receiveSocketChannel = receiverConnector.getClientSocketChannel();
            TCPReceiver tcpReceiver = new TCPReceiver(this, tokens[4], receiveSocketChannel, tokens[1], Double.parseDouble(tokens[2]), downloadFrame);
            tcpReceiver.start();

        } else {

            System.err.println("error - keys do not match.");
            String response = "KeyMismatch " + tokens[4];
            write(response);

            key = null;
            receiving = false;
            sendingPeer = null;

            clientFrame.throwError("keys do not match");
        }
    }

    private void finishedSending() {
        sending = false;
        sharedFile = null;
        receivingPeer = null;
        uploadFrame.setProgress(0);
        uploadFrame.setVisible(false);
    }

    private void finishedDownloading(String line) throws IOException {
        write(line);

        key = null;
        receiving = false;
        sendingPeer = null;
        clientFrame.clearTable();
        serverSocketChannel.close();
        downloadFrame.setProgress(0);
        downloadFrame.setVisible(false);
    }

    private void handleErrors(String line) {
        if (line.contains("nickname already taken.")) {
            loginFrame.throwError("nickname already taken");
        } else {
            clientFrame.throwError(line.replace("error - ", ""));
        }
    }

    public void write(String message) {
        try {
            System.out.println("Message: " + message);
            String encryptedMessage = rsa.encrypt(message) + "\n";
            System.out.println("Encrypted message sent: " + encryptedMessage);
            outputStream.write(encryptedMessage.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchFrame(String frame) {
        if (frame.equals("chat")) {
            chatFrame = new ChatRoomFrame(this, onlineUsers.toArray(new String[0]), broadcasts.toArray(new String[0]));
            java.awt.EventQueue.invokeLater(() -> chatFrame.setVisible(true));
        } else {
            chatFrame.dispose();
            chatFrame = null;
            statusListener = null;
            broadcastListener = null;
            java.awt.EventQueue.invokeLater(() -> clientFrame.setVisible(true));
        }
    }

    public void openChatWindow(String participant) {
        ArrayList<String> messages;

        if ((messages = messageHistory.get(participant)) == null) {
            messages = new ArrayList<>();
            messageHistory.put(participant, messages);
        }

        ChatWindowFrame chatWindow;

        if ((chatWindow = chatWindows.get(participant)) == null) {

            chatWindow = new ChatWindowFrame(this, participant, messages.toArray(new String[0]));
            chatWindows.put(participant, chatWindow);

        } else {

            chatWindow.setVisible(false);
            chatWindow.dispose();

            chatWindow = new ChatWindowFrame(this, participant, messages.toArray(new String[0]));
            chatWindows.replace(participant, chatWindow);
        }

        chatWindow.setVisible(true);
    }

    public void setStatusListener(StatusListener sL) {
        statusListener = sL;
    }

    public void setBroadcastListener(MessageListener bL) {
        broadcastListener = bL;
    }

    public void addMessageListener(String nickname, MessageListener mL) {
        messageListeners.remove(nickname);
        messageListeners.put(nickname, mL);
    }

    private void handleOnlineStatus(String[] tokens) {
        statusListener.online(tokens[1]);
    }

    private void handleOfflineStatus(String[] tokens) {
        statusListener.offline(tokens[1]);
    }

    private String generateSecretKey() {
        StringBuilder key = new StringBuilder();
        String symbols = "0123456789aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ:!@#$%^&*(){}[]";
        for (int i = 0; i < 16; i++) {
            key.append(symbols.charAt((int) (Math.random() * (symbols.length() - 1))));
        }
        return key.toString();
    }
}
