package com.info121.nativelimo.models;

public class RequestUpdateChannelID {
    String newchannelid;

    public String getNewchannelid() {
        return newchannelid;
    }

    public void setNewchannelid(String newchannelid) {
        this.newchannelid = newchannelid;
    }

    public RequestUpdateChannelID(String newchannelid) {
        this.newchannelid = newchannelid;
    }
}
