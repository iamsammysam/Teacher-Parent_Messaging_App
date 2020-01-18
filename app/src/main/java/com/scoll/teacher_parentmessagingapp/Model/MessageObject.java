package com.scoll.teacher_parentmessagingapp.Model;

import java.util.Date;

public class MessageObject {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String message;
    // private long messageTime;

    // constructor for the chatObject
    public MessageObject(String messageId, String senderId, String receiverId, String message){
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;

        // Initialize to current time
//        this.messageTime = new Date().getTime();
    }

    // (getters) functions that returns the variables
    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getReceiverId() {return receiverId;}
    public String getMessage() {return message;}
    // public long getMessageTime() {return messageTime;}
}