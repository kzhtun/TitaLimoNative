package com.info121.nativelimo.models;

public class RequestMobileLog {
    String stacktracelog;
    String remarks;

    public RequestMobileLog(String stacktracelog, String remarks) {
        this.stacktracelog = stacktracelog;
        this.remarks = remarks;
    }

    public String getStacktracelog() {
        return stacktracelog;
    }

    public void setStacktracelog(String stacktracelog) {
        this.stacktracelog = stacktracelog;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
