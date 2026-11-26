package com.example.livestreambackend.controller;

import com.example.livestreambackend.dto.CompanyInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds the company details to the model of every page as "company",
 * so the shared footer can show the name, CVR number and links on all pages
 * without each controller having to add them.
 */
@ControllerAdvice
public class CompanyInfoAdvice {

    private final CompanyInfo companyInfo;

    public CompanyInfoAdvice(@Value("${company.name:[Virksomhedens navn]}") String name,
                             @Value("${company.cvr:[CVR-nummer]}") String cvr,
                             @Value("${company.address:[Adresse]}") String address,
                             @Value("${company.email:[Kontakt-e-mail]}") String email) {
        this.companyInfo = new CompanyInfo(name, cvr, address, email);
    }

    @ModelAttribute("company")
    public CompanyInfo company() {
        return companyInfo;
    }
}
