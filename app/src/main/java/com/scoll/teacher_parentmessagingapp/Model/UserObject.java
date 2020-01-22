package com.scoll.teacher_parentmessagingapp.Model;

public class UserObject {
    private String uid;
    private String username;
    private String phoneNumber;
    private String userLanguage;

    public UserObject() {}

    // constructor for the userObject
    public UserObject(String uid, String username, String phoneNumber, String userLanguage){
        this.uid = uid;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.userLanguage = userLanguage;
    }

    // (getters) functions that returns the variables
    public String getPhoneNumber() {return phoneNumber;}
    public String getLanguage() {return userLanguage;}
    public String getUsername() {return username;}
    public String getUid() {return uid;}

    public void setUsername(String username) {
        this.username = username;
    }
}
