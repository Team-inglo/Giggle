package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inglo.giggle.annotation.Date;

import java.time.LocalDateTime;

public record UpdateApplicantDto(
        @JsonProperty("address_name")
        String addressName,
        @JsonProperty("address_x")
        Float addressX,
        @JsonProperty("address_y")
        Float addressY,
        @JsonProperty("gpa")
        String gpa,
        @JsonProperty("startDay")
        @Date
        LocalDateTime startDay,
        @JsonProperty("endDay")
        @Date
        LocalDateTime endDay,
        @JsonProperty("semester")
        Integer semester
) {
}
