package client.gui;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

/**
 * @author rayquaza
 */
public class ConnectFrame extends javax.swing.JFrame {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField connectField;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form ConnectFrame
     */
    public ConnectFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectField = new javax.swing.JTextField();
        javax.swing.JPanel panel = new javax.swing.JPanel();
        javax.swing.JLabel header = new javax.swing.JLabel();
        javax.swing.JLabel subtitle = new javax.swing.JLabel();
        javax.swing.JButton connectButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(555, 250));

        setTitle("P2P File Transfer App");

        header.setText("CONNECT");

        subtitle.setText("Please enter a server address:");

        connectButton.setText("Connect");
        connectButton.addActionListener(this::connectButtonActionPerformed);

        connectField.addActionListener(this::actionPerformed);

        panel.add(header);
        panel.add(subtitle);
        panel.add(connectField);
        panel.add(connectButton);

        javax.swing.GroupLayout pLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(pLayout);
        pLayout.setHorizontalGroup(
                pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pLayout.createSequentialGroup()
                                .addGroup(pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pLayout.createSequentialGroup()
                                                .addGap(221, 221, 221)
                                                .addComponent(header))
                                        .addGroup(pLayout.createSequentialGroup()
                                                .addGap(218, 218, 218)
                                                .addComponent(connectButton))
                                        .addGroup(pLayout.createSequentialGroup()
                                                .addGap(149, 149, 149)
                                                .addComponent(subtitle))
                                        .addGroup(pLayout.createSequentialGroup()
                                                .addGap(174, 174, 174)
                                                .addComponent(connectField, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(164, Short.MAX_VALUE))
        );
        pLayout.setVerticalGroup(
                pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(header)
                                .addGap(18, 18, 18)
                                .addComponent(subtitle)
                                .addGap(18, 18, 18)
                                .addComponent(connectField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(connectButton)
                                .addContainerGap(42, Short.MAX_VALUE))
        );

        header.setBounds(150, 30, 300, 50);
        header.setFont(new Font("Verdana", Font.BOLD, 45));
        subtitle.setBounds(50, 90, 300, 30);
        subtitle.setFont(new Font("Verdana", Font.ITALIC, 15));
        connectField.setBounds(290, 90, 200, 35);
        connectField.setFont(new Font("Verdana", Font.PLAIN, 16));
        connectButton.setBounds(220, 145, 110, 40);
        connectButton.setFont(new Font("Verdana", Font.BOLD, 13));

        panel.setLayout(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        setBounds(0, 0, 555, 250);
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        connect();
    }//GEN-LAST:event_connectButtonActionPerformed

    private void actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionPerformed
        connect();
    }//GEN-LAST:event_actionPerformed

    private void connect() {
        if (connectField.getText().length() > 0) {
            try {

                Socket socket = new Socket(connectField.getText(), 2620);

                System.out.println("Connected to server ...\n");

                Client client = new Client(socket);
                client.start();

                this.setVisible(false);

            } catch (IOException e) {
                System.err.println("server not found");
                JOptionPane.showMessageDialog(null, "server not found", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a server address.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
