package com.scoll.teacher_parentmessagingapp;

public class MessageObject {
    String messageId;
    String senderId;
    String message;


    // constructor for the chatObject
    public MessageObject(String messageId, String senderId, String message){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
    }

    // function that returns the variables as public (getters)
    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getMessage() {return message;}
}