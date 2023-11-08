package com.info121.nativelimo.models;

public class PatientSearchParams {
        String customercode ;
        String datefrom ;
        String dateto ;
        String sort ;


    public PatientSearchParams(String customercode, String datefrom, String dateto, String sort) {
        this.customercode = customercode;
        this.datefrom = datefrom;
        this.dateto = dateto;
        this.sort = sort;
    }

    public String getCustomercode() {
        return (customercode.length() == 0 ) ? " " : customercode;
    }

    public void setCustomercode(String customercode) {
        this.customercode = customercode;
    }

    public String getDatefrom() {
        return (datefrom.length() == 0 ) ? " " : datefrom;
    }

    public void setDatefrom(String datefrom) {
        this.datefrom = datefrom;
    }

    public String getDateto() {
        return (dateto.length() == 0 ) ? " " : dateto;
    }

    public void setDateto(String dateto) {
        this.dateto = dateto;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
