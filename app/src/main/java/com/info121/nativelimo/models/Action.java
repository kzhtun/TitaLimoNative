package com.info121.nativelimo.models;

public class Action {
    private String action = "";
    private String jobNo = "";

    public Action(String action, String jobNo) {
        this.action = action;
        this.jobNo = jobNo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }
}
