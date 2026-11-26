package com.example.livestreambackend.controller;

import com.example.livestreambackend.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LegalController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "company.name=Testfirma ApS",
        "company.cvr=12345678",
        "company.address=Testvej 1, 1000 København K",
        "company.email=kontakt@test.dk"
})
class LegalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void terms_isPublicAndShowsCompanyDetails() throws Exception {
        mockMvc.perform(get("/terms"))
                .andExpect(status().isOk())
                .andExpect(view().name("terms"))
                .andExpect(content().string(containsString("Handelsbetingelser")))
                .andExpect(content().string(containsString("Testfirma ApS")))
                .andExpect(content().string(containsString("12345678")));
    }

    @Test
    void privacy_isPublicAndShowsCompanyDetails() throws Exception {
        mockMvc.perform(get("/privacy"))
                .andExpect(status().isOk())
                .andExpect(view().name("privacy"))
                .andExpect(content().string(containsString("Privatlivspolitik")))
                .andExpect(content().string(containsString("Testfirma ApS")));
    }

    @Test
    void footer_hasCvrAndLinksToBothPages() throws Exception {
        mockMvc.perform(get("/terms"))
                .andExpect(content().string(containsString("CVR:")))
                .andExpect(content().string(containsString("href=\"/terms\"")))
                .andExpect(content().string(containsString("href=\"/privacy\"")));
    }

    @Test
    void companyDetailsAreAddedToTheModel() throws Exception {
        mockMvc.perform(get("/privacy"))
                .andExpect(model().attributeExists("company"));
    }
}
