package client.listener;

public interface StatusListener {
    void online(String nickname);

    void offline(String nickname);
}