package com.scoll.teacher_parentmessagingapp.Model;

public class ChatObject {
    private String chatId;
    private String title;

    // constructor for the chatObject
    public ChatObject(String chatId, String title){
        this.chatId = chatId;
        this.title = title;
    }

    // (getters) functions that returns the variables
    public String getChatId() {return chatId;}
    public String getTitle() {return title;}
}
