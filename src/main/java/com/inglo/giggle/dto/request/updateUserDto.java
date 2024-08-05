package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inglo.giggle.annotation.Date;

import java.time.LocalDateTime;

public record UpdateUserDto(
        @JsonProperty("address")
        String address,
        @JsonProperty("gpa")
        String gpa,
        @JsonProperty("startDay")
        @Date
        LocalDateTime startDay,
        @JsonProperty("endDay")
        @Date
        LocalDateTime endDay
) {
}
