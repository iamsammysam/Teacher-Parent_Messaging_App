package com.scoll.teacher_parentmessagingapp;

public class ChatObject {
    private String chatId;
//    private String title;

    // constructor for the chatObject
    public ChatObject(String chatId){
        this.chatId = chatId;
//        this.title = title;
    }

    // function that returns the variables as public
    public String getChatId() {return chatId;}
//    public String getTitle() {return title;}
}