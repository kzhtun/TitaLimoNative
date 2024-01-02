package com.info121.nativelimo.models;

public class RequestUpdateRemark {
    String jobno;
    String remarks;

    public RequestUpdateRemark(String jobNo, String remarks) {
        this.jobno = jobNo;
        this.remarks = remarks;
    }

    public String getJobNo() {
        return jobno;
    }

    public void setJobNo(String jobNo) {
        this.jobno = jobNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
