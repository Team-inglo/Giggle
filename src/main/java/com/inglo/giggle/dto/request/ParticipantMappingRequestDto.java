package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ParticipantMappingRequestDto(
        @JsonProperty("signingMethod") @Schema(description = "서명 방법", required = true)
        @NotNull(message = "서명 방법은 null이 될 수 없습니다.")
        WebClientRequestDto.SigningMethod signingMethod,

        @JsonProperty("role") @Schema(description = "역할", required = true)
        @NotNull(message = "역할은 null이 될 수 없습니다.")
        String role,

        @JsonProperty("name") @Schema(description = "이름", required = true)
        @NotNull(message = "이름은 null이 될 수 없습니다.")
        String name
) {
    public record SigningMethod(
            @JsonProperty("type") @Schema(description = "서명 방법 타입", required = true)
            @NotNull(message = "서명 방법 타입은 null이 될 수 없습니다.")
            String type,

            @JsonProperty("value") @Schema(description = "서명 방법 값", required = true)
            @NotNull(message = "서명 방법 값은 null이 될 수 없습니다.")
            String value
    ) {}
}
