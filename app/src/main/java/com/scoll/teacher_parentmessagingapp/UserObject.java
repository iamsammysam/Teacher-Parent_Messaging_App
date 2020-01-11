package com.scoll.teacher_parentmessagingapp;

public class UserObject {
    //private String uid;
    private String name;
    private String phoneNumber;

    // constructor for the userObject
    public UserObject(String name, String phoneNumber){
//        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // function that returns the variables to us as public
    public String getPhoneNumber() {return phoneNumber;}
    public String getName() {return name;}
    //public String getUid() {return uid;}


    public void setName(String name) {
        this.name = name;
    }
}
