package client.handler;

import client.Client;

/**
 * @author rayquaza
 */
public class OnExit extends Thread {
    private final Client client;

    public OnExit(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        client.writeCommand("quit");
    }

}
