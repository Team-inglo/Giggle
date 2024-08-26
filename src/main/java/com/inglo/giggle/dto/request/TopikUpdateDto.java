package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopikUpdateDto(
        @JsonProperty("topik_score")
        String topikScore
) {
}