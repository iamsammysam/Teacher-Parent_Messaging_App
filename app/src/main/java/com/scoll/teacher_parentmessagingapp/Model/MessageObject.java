package com.scoll.teacher_parentmessagingapp.Model;

import java.util.Date;

public class MessageObject {
    private String message;
    private String messageId;
    private String creatorId;
    private String username;
    private String language;
    // private long messageTime;


    // constructor for the chatObject
    public MessageObject(String messageId, String creatorId, String creatorUsername, String message, String language){
        this.message = message;
        this.messageId = messageId;
        this.creatorId = creatorId;
        this.language = language;
        this.username = creatorUsername;

        // Initialize to current time
//        this.messageTime = new Date().getTime();
    }

    // (getters) functions that returns the variables
    public String getMessage() {return message;}
    public String getMessageId() {return messageId;}
    public String getSenderId() {return creatorId;}
    public String getLanguage() {return language;}
    public String getSenderUsername() {return username;}

    // public long getMessageTime() {return messageTime;}
}