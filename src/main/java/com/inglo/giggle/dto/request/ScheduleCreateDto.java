package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ScheduleCreateDto(
        @JsonProperty(value = "start_at")
        LocalDateTime startAt,
        @JsonProperty(value = "end_at")
        LocalDateTime endAt
) {
}