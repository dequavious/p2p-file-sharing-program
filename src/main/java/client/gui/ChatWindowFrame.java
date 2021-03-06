package client.gui;

import client.Client;
import client.listener.MessageListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * @author rayquaza
 */
public class ChatWindowFrame extends javax.swing.JFrame implements MessageListener {

    private final Client client;
    private final String participant;
    private final String[] messages;
    private final DefaultListModel<String> chatModel = new DefaultListModel<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField inputField;
    private javax.swing.JScrollPane chatScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form ChatWindowFrame
     *
     * @param client      current client
     * @param participant other participant in the private chat
     * @param messages    chat history
     */
    public ChatWindowFrame(Client client, String participant, String[] messages) {
        this.client = client;
        this.participant = participant;
        this.messages = messages;
        initComponents();
        initListeners();
    }

    private void initListeners() {
        client.addMessageListener(participant, this);
        for (String message : messages) {
            message(message);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputField = new javax.swing.JTextField();
        chatScrollPane = new javax.swing.JScrollPane();
        javax.swing.JPanel chatPanel = new javax.swing.JPanel();
        javax.swing.JList<String> chatList = new javax.swing.JList<>(chatModel);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(participant);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                windowListener();
            }
        });

        chatList.setFont(new Font("Verdana", Font.PLAIN, 17));
        chatScrollPane.setViewportView(chatList);

        inputField.addActionListener(this::actionPerformed);
        inputField.setFont(new Font("Verdana", Font.PLAIN, 20));

        javax.swing.GroupLayout chatPanelLayout = new javax.swing.GroupLayout(chatPanel);
        chatPanel.setLayout(chatPanelLayout);
        chatPanelLayout.setHorizontalGroup(
                chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(chatPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                                        .addComponent(inputField))
                                .addContainerGap())
        );
        chatPanelLayout.setVerticalGroup(
                chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(chatPanelLayout.createSequentialGroup()
                                .addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scrollToBottom() {
        JScrollBar scrollBar = chatScrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                scrollBar.removeAdjustmentListener(this);
            }
        };
        scrollBar.addAdjustmentListener(downScroller);
    }

    private void actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionPerformed
        String text = inputField.getText();
        if (text.length() > 0) {
            client.writeCommand("dm " + participant + " " + text);
            chatModel.addElement("You: " + text);
            inputField.setText("");
            scrollToBottom();
        }
    }//GEN-LAST:event_actionPerformed

    private void windowListener() {//GEN-FIRST:event_windowListener
        setFocusable(false);
    }//GEN-LAST:event_windowListener

    @Override
    public void message(String message) {
        chatModel.addElement(message);
        scrollToBottom();
    }
}
