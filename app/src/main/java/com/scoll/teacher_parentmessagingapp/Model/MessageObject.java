package com.scoll.teacher_parentmessagingapp.Model;

import java.util.Date;

public class MessageObject {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String message;
    //private String translation;
    // private long messageTime;


    // constructor for the chatObject
    public MessageObject(String messageId, String senderId, String receiverId, String message){
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        //this.translation = translation;

        // Initialize to current time
//        this.messageTime = new Date().getTime();
    }

    // (getters) functions that returns the variables
    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getReceiverId() {return receiverId;}
    public String getMessage() {return message;}
    // public String getTranslation() {return translation;}
    // public long getMessageTime() {return messageTime;}
}