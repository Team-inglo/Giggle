package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RegistrationUpdateDto(
        @JsonProperty("registration_number")
        String registrationNumber,
        @JsonProperty("status_of_residence")
        String statusOfResidence,
        @JsonProperty("registration_issue_date")
        String registrationIssueDate
) {
}
