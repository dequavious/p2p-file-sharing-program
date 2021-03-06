package client.gui;

import client.Client;
import client.listener.MessageListener;
import client.listener.StatusListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * @author rayquaza
 */
public class ChatRoomFrame extends javax.swing.JFrame implements StatusListener, MessageListener {

    private final Client client;
    private final String[] users;
    private final String[] broadcasts;
    private final DefaultListModel<String> chatModel = new DefaultListModel<>();
    private final DefaultListModel<String> usersModel = new DefaultListModel<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice choice;
    private javax.swing.JTextField inputField;
    private javax.swing.JList<String> usersList;
    private javax.swing.JScrollPane chatScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form ChatRoomFrame
     *
     * @param client current client
     * @param users  online users
     */
    public ChatRoomFrame(Client client, String[] users, String[] broadcasts) {
        this.client = client;
        this.users = users;
        this.broadcasts = broadcasts;
        initComponents();
        initListeners();
    }

    private void initListeners() {
        client.setStatusListener(this);
        for (String user : users) {
            online(user);
        }

        client.setBroadcastListener(this);
        for (String message : broadcasts) {
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

        choice = new java.awt.Choice();
        inputField = new javax.swing.JTextField();
        chatScrollPane = new javax.swing.JScrollPane();
        usersList = new javax.swing.JList<>(usersModel);
        javax.swing.JPanel usersPanel = new javax.swing.JPanel();
        javax.swing.JPanel broadcastPanel = new javax.swing.JPanel();
        javax.swing.JList<String> chatList = new javax.swing.JList<>(chatModel);
        javax.swing.JScrollPane usersScrollPane = new javax.swing.JScrollPane();
        TitledBorder chatBorder = new TitledBorder("Public chat:");
        TitledBorder usersBorder = new TitledBorder("Online users:");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ChatRoom");

        chatList.setFont(new Font("Verdana", Font.PLAIN, 17));
        chatScrollPane.setViewportView(chatList);

        inputField.addActionListener(this::actionPerformed);
        inputField.setFont(new Font("Verdana", Font.PLAIN, 20));

        chatBorder.setTitleJustification(TitledBorder.CENTER);
        chatBorder.setTitlePosition(TitledBorder.TOP);
        chatBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16));

        javax.swing.GroupLayout broadcastPanelLayout = new javax.swing.GroupLayout(broadcastPanel);
        broadcastPanel.setLayout(broadcastPanelLayout);
        broadcastPanel.setBorder(chatBorder);
        broadcastPanelLayout.setHorizontalGroup(
                broadcastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(broadcastPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(broadcastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                                        .addComponent(inputField))
                                .addContainerGap())
        );
        broadcastPanelLayout.setVerticalGroup(
                broadcastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(broadcastPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        usersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ChatRoomFrame.this.mouseClicked(evt);
            }
        });
        usersList.setFont(new Font("Verdana", Font.PLAIN, 17));
        usersScrollPane.setViewportView(usersList);

        usersBorder.setTitleJustification(TitledBorder.CENTER);
        usersBorder.setTitlePosition(TitledBorder.TOP);
        usersBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16));

        javax.swing.GroupLayout usersPanelLayout = new javax.swing.GroupLayout(usersPanel);
        usersPanel.setLayout(usersPanelLayout);
        usersPanel.setBorder(usersBorder);
        usersPanelLayout.setHorizontalGroup(
                usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(usersPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(usersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                .addContainerGap())
        );
        usersPanelLayout.setVerticalGroup(
                usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(usersPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(usersScrollPane)
                                .addContainerGap())
        );

        choice.add("ChatRoom");
        choice.add("Transfers");
        choice.addItemListener(this::chooseItem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(choice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap()
                                                .addComponent(broadcastPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(usersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(choice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(broadcastPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(usersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void chooseItem(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chooseItem
        if (choice.getSelectedItem().equals("Transfers")) {
            choice.select("ChatRoom");
            setVisible(false);
            client.switchFrame("transfer");
        }
    }//GEN-LAST:event_chooseItem

    private void actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionPerformed
        String text = inputField.getText();
        if (text.length() > 0) {
            client.writeCommand("bc " + text);
            chatModel.addElement("You: " + text);
            inputField.setText("");
            scrollToBottom();
        }
    }//GEN-LAST:event_actionPerformed

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (evt.getClickCount() > 1) {
            String participant = usersList.getSelectedValue();
            client.openChatWindow(participant);
        }
    }//GEN-LAST:event_mouseClicked

    @Override
    public void online(String nickname) {
        usersModel.addElement(nickname);
    }

    @Override
    public void offline(String nickname) {
        usersModel.removeElement(nickname);
    }

    @Override
    public void message(String message) {
        chatModel.addElement(message);
        scrollToBottom();
    }
}
