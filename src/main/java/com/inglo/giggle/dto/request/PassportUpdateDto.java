package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PassportUpdateDto(
        @JsonProperty("passport_number")
        String passportNumber,
        @JsonProperty("name")
        String name,
        @JsonProperty("sex")
        String sex,
        @JsonProperty("date_of_birth")
        String dateOfBirth,
        @JsonProperty("nationality")
        String nationality,
        @JsonProperty("passport_issue_date")
        String passportIssueDate,
        @JsonProperty("passport_expiry_date")
        String passportExpiryDate
) {
}
