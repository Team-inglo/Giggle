package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebClientEmbeddedResponseDto(
        @JsonProperty("embeddedUrl") String embeddedUrl
) {
}
