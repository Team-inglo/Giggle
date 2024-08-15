package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(name = "WebClientResponseDTO", description = "모두싸인 Response DTO")
public record WebClientResponseDto (
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("status") String status,
        @JsonProperty("requester") Requester requester,
        @JsonProperty("participants") List<Participant> participants,
        @JsonProperty("currentSigningOrder") int currentSigningOrder,
        @JsonProperty("signings") List<Signing> signings,
        @JsonProperty("accessibleByParticipant") boolean accessibleByParticipant,
        @JsonProperty("abort") Object abort,
        @JsonProperty("metadatas") List<Object> metadatas,
        @JsonProperty("file") File file,
        @JsonProperty("auditTrail") Object auditTrail,
        @JsonProperty("updatedAt") LocalDateTime updatedAt,
        @JsonProperty("createdAt") LocalDateTime createdAt
) {
    public record Requester(
            @JsonProperty("email") String email,
            @JsonProperty("name") String name
    ) {}

    public record Participant(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("signingOrder") int signingOrder,
            @JsonProperty("signingDue") SigningDue signingDue,
            @JsonProperty("signingMethod") SigningMethod signingMethod,
            @JsonProperty("locale") String locale
    ) {}

    public record SigningDue(
            @JsonProperty("valid") boolean valid,
            @JsonProperty("datetime") LocalDateTime datetime
    ) {}

    public record SigningMethod(
            @JsonProperty("type") String type,
            @JsonProperty("value") String value
    ) {}

    public record Signing(
            // Define the fields for Signing class as per the API response
    ) {}

    public record File(
            @JsonProperty("downloadUrl") String downloadUrl
    ) {}
}