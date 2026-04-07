package com.buet.bibliotech;

public class MessageModel {
    private int messageID;
    private String senderID;
    private String receiverID;
    private String messageText;
    private String timestamp;

    public MessageModel(int messageID, String senderID, String receiverID, String messageText, String timestamp) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // Getters
    public int getMessageID() { return messageID; }
    public String getSenderID() { return senderID; }
    public String getReceiverID() { return receiverID; }
    public String getMessageText() { return messageText; }
    public String getTimestamp() { return timestamp; }
}