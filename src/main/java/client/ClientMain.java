package client;

import client.gui.ConnectFrame;

public class ClientMain {
    public static void main(String[] args) {
        ConnectFrame connectFrame = new ConnectFrame();

        java.awt.EventQueue.invokeLater(() -> connectFrame.setVisible(true));
    }
}