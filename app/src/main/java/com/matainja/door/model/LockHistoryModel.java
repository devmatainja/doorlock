package com.matainja.door.model;


public class LockHistoryModel {



    private String username,Lockdatetime,windowStatus,doorStatus,userId,key;

    public LockHistoryModel() {


    }


    public String getWindowStatus() {
        return windowStatus;
    }

    public void setWindowStatus(String windowStatus) {
        this.windowStatus = windowStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLockdatetime() {
        return Lockdatetime;
    }

    public void setLockdatetime(String lockdatetime) {
        Lockdatetime = lockdatetime;
    }

    public String getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(String doorStatus) {
        this.doorStatus = doorStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}