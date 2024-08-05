package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record updateUserDto(
        @JsonProperty("address")
        String address
) {
}
