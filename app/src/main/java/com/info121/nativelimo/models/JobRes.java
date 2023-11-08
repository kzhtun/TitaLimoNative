package com.info121.nativelimo.models;

import java.util.List;

public class JobRes {
    private Object jobcount;
    private List<JobCount> jobcountlist;
    private List<Job> jobs;
    private List<Patient> patients;
    private Job jobdetails;
    private String responsemessage;
    private String status;
    private String token;

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public Object getJobcount() {
        return jobcount;
    }

    public void setJobcount(Object jobcount) {
        this.jobcount = jobcount;
    }

    public List<JobCount> getJobcountlist() {
        return jobcountlist;
    }

    public void setJobcountlist(List<JobCount> jobcountlist) {
        this.jobcountlist = jobcountlist;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public String getResponsemessage() {
        return responsemessage;
    }

    public void setResponsemessage(String responsemessage) {
        this.responsemessage = responsemessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Job getJobdetails() {
        return jobdetails;
    }

    public void setJobdetails(Job jobdetails) {
        jobdetails = jobdetails;
    }

    @Override
    public String toString() {
        return
                "JobRes{" +
                        "jobcount = '" + jobcount + '\'' +
                        ",jobcountlist = '" + jobcountlist + '\'' +
                        ",jobs = '" + jobs + '\'' +
                        ",responsemessage = '" + responsemessage + '\'' +
                        ",status = '" + status + '\'' +
                        ",token = '" + token + '\'' +
                        "}";
    }
}