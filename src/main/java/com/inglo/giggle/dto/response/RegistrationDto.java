package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RegistrationDto(
        String registrationNumber,
        String statusOfResidence,
        String registrationIssueDate
) {
}
