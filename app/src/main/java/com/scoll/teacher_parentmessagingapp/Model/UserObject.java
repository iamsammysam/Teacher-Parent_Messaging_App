package com.scoll.teacher_parentmessagingapp.Model;

public class UserObject {
    private String uid;
    private String username;
    private String phoneNumber;

    public UserObject() {}

    // constructor for the userObject
    public UserObject(String uid, String username, String phoneNumber){
        this.uid = uid;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    // (getters) functions that returns the variables
    public String getPhoneNumber() {return phoneNumber;}
    public String getUsername() {return username;}
    public String getUid() {return uid;}

    public void setUsername(String username) {
        this.username = username;
    }
}
