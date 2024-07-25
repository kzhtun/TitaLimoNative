package com.info121.nativelimo.models;

public class RequestValidateDriver {
    String driver;
    String password;

    public RequestValidateDriver(String driver, String password) {
        this.driver = driver;
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
