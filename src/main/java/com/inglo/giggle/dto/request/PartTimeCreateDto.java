package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PartTimeCreateDto(
        @JsonProperty(value = "name")
        String name,
        @JsonProperty(value = "hourly_rate")
        Integer hourlyRate,
        @JsonProperty(value = "color")
        String color
) {
}