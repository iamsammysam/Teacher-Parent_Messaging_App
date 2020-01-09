package com.scoll.teacher_parentmessagingapp;

public class UserObject {
    private String uid;
    private String name;
    private String phoneNumber;


    public UserObject(String uid, String name, String phoneNumber){
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {return phoneNumber;}
    public String getName() {return name;}
    public String getUid() {return uid;}
}
