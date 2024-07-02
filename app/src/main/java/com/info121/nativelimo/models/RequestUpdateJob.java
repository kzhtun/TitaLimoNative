package com.info121.nativelimo.models;

public class RequestUpdateJob {
    String jobno;
    String jobloc;
    String remarks;
    String status;
    String base64signature;
    String base64photo;

    public String getJobno() {
        return jobno;
    }

    public void setJobno(String jobno) {
        this.jobno = jobno;
    }

    public String getJobloc() {
        return jobloc;
    }

    public void setJobloc(String jobloc) {
        this.jobloc = jobloc;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBase64signature() {
        return base64signature;
    }

    public void setBase64signature(String base64signature) {
        this.base64signature = base64signature;
    }

    public String getBase64photo() {
        return base64photo;
    }

    public void setBase64photo(String base64photo) {
        this.base64photo = base64photo;
    }
}
