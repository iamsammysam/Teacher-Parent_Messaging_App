package com.scoll.teacher_parentmessagingapp;

public class MessageObject {
    private String messageId;
    private String senderId;
    private String message;

    // constructor for the chatObject
    public MessageObject(String messageId, String senderId, String message){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
    }

    // (getters) functions that returns the variables
    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getMessage() {return message;}
}