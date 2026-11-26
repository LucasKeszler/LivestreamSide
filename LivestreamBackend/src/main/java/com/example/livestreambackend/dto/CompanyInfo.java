package com.example.livestreambackend.dto;

/**
 * The seller's company details, shown in the footer and on the terms and privacy pages.
 * The values come from application.properties (company.*), so they are changed in one place.
 */
public class CompanyInfo {

    private final String name;
    private final String cvr;
    private final String address;
    private final String email;

    public CompanyInfo(String name, String cvr, String address, String email) {
        this.name = name;
        this.cvr = cvr;
        this.address = address;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getCvr() {
        return cvr;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }
}
