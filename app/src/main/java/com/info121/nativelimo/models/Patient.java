package com.info121.nativelimo.models;

public class Patient {
    String JobNo ;
    String JobType ;
    String JobDate ;
    String JobTime ;
    String PickupPoint ;
    String AlightPoint ;
    String Translator ;
    String Updates ;

    public String getJobType() {
        return JobType;
    }

    public void setJobType(String jobType) {
        JobType = jobType;
    }

    public String getJobDate() {
        return JobDate;
    }

    public void setJobDate(String jobDate) {
        JobDate = jobDate;
    }

    public String getJobNo() {
        return JobNo;
    }

    public void setJobNo(String jobNo) {
        JobNo = jobNo;
    }

    public String getJobTime() {
        return JobTime;
    }

    public void setJobTime(String jobTime) {
        JobTime = jobTime;
    }

    public String getPickupPoint() {
        return PickupPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        PickupPoint = pickupPoint;
    }

    public String getAlightPoint() {
        return AlightPoint;
    }

    public void setAlightPoint(String alightPoint) {
        AlightPoint = alightPoint;
    }

    public String getTranslator() {
        return Translator;
    }

    public void setTranslator(String translator) {
        Translator = translator;
    }

    public String getUpdates() {
        return Updates;
    }

    public void setUpdates(String updates) {
        Updates = updates;
    }
}
