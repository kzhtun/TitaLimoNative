package com.info121.nativelimo.models;

public class RequestUpdatePassword {
    String newpassword;

    public RequestUpdatePassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
