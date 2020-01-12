package com.scoll.teacher_parentmessagingapp;

public class UserObject {
    private String uid;
    private String name;
    private String phoneNumber;

    // constructor for the userObject
    public UserObject(String uid, String name, String phoneNumber){
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // (getters) functions that returns the variables
    public String getPhoneNumber() {return phoneNumber;}
    public String getName() {return name;}
    public String getUid() {return uid;}


    public void setName(String name) {
        this.name = name;
    }
}
