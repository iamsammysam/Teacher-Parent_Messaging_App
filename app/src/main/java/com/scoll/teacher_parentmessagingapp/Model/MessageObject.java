package com.scoll.teacher_parentmessagingapp.Model;

import java.util.Date;

public class MessageObject {
    private String messageId;
    private String senderId;
    private String message;
    // private long messageTime;


    // constructor for the chatObject
    public MessageObject(String messageId, String senderId, String message, String messageTime){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;

        // Initialize to current time
//        this.messageTime = new Date().getTime();
    }

    // (getters) functions that returns the variables
    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getMessage() {return message;}
    // public long getMessageTime() {return messageTime;}

}