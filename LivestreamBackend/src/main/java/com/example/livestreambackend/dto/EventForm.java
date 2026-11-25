package com.example.livestreambackend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Data fra "Opret event"-formularen i admin. */
public class EventForm {

    @NotNull(message = "År skal udfyldes")
    @Min(value = 2000, message = "Året skal være 2000 eller senere")
    @Max(value = 2100, message = "Året skal være 2100 eller tidligere")
    private Integer year;

    @NotBlank(message = "Titel skal udfyldes")
    @Size(max = 255, message = "Titlen må højst være 255 tegn")
    private String title;

    @NotBlank(message = "Sted skal udfyldes")
    @Size(max = 255, message = "Stedet må højst være 255 tegn")
    private String location;

    @NotNull(message = "Årspaspris skal udfyldes")
    @DecimalMin(value = "0.00", inclusive = false, message = "Prisen skal være større end 0")
    @Digits(integer = 8, fraction = 2, message = "Prisen må højst have 2 decimaler")
    private BigDecimal yearPassPrice;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getYearPassPrice() {
        return yearPassPrice;
    }

    public void setYearPassPrice(BigDecimal yearPassPrice) {
        this.yearPassPrice = yearPassPrice;
    }
}
