package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SocialIntegrationProgramUpdateDto(
        @JsonProperty("social_integration_program_score")
        String socialIntegrationProgramScore
) {
}
