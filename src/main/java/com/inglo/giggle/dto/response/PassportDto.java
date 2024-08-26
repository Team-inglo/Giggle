package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PassportDto(
        String passportNumber,
        String name,
        String sex,
        String dateOfBirth,
        String nationality,
        String passportIssueDate,
        String passportExpiryDate
) {
}
