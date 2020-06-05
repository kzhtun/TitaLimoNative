package com.info121.nativelimo.models;

public class SearchParams {
    String customer ;
    String fromDate ;
    String toDate ;
    String sort ;

    public SearchParams(String customer, String fromDate, String toDate, String sort) {
        this.customer = customer;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.sort = sort;
    }

    public String getCustomer() {
        return (customer.length() == 0 ) ? " " : customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getFromDate() {
        return (fromDate.length() == 0 ) ? " " : fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return (toDate.length() == 0 ) ? " " : toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
