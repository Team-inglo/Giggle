package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record WebHookRequestDto(
        @JsonProperty("event") @Schema(description = "이벤트", required = true)
        Event event,
        @JsonProperty("document") @Schema(description = "문서", required = true)
        Document document
) {
    public record Event(
            @JsonProperty("type") @Schema(description = "타입", required = true)
            String type
    ) {

    }

    public record Document(
            @JsonProperty("id") @Schema(description = "문서ID", required = true)
            String id,
            @JsonProperty("request") @Schema(description = "요청자", required = true)
            Requester requester
    ) {
        public record Requester(
                @JsonProperty("email") @Schema(description = "요청자이메일", required = true)
                String email
        ) {

        }
    }
}
