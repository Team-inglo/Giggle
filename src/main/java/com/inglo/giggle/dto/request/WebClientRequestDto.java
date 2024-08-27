package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WebClientRequestDto(
        @JsonProperty("document")
        @Schema(description = "문서 정보", required = true)
        @NotNull(message = "문서 정보는 null이 될 수 없습니다.")
        Document document,

        @JsonProperty("templateId")
        @Schema(description = "템플릿 ID", example = "173d10b0-46a9-11ef-82c8-6b94a46e1b38", required = true)
        @NotNull(message = "템플릿 ID는 null이 될 수 없습니다.")
        String templateId
) {
    public record Document(
            @JsonProperty("participantMappings")
            @Schema(description = "참여자 매핑 목록", required = true)
            @NotNull(message = "참여자 매핑 목록은 null이 될 수 없습니다.")
            List<ParticipantMapping> participantMappings,

            @JsonProperty("requesterInputMappings")
            @Schema(description = "요청자 데이터 입력 매핑 목록", required = false)
            List<RequesterInputMapping> requesterInputMappings,

            @JsonProperty("title")
            @Schema(description = "문서 제목", example = "시간제취업허가서", required = true)
            @NotNull(message = "문서 제목은 null이 될 수 없습니다.")
            String title
    ) {}

    public record ParticipantMapping(
            @JsonProperty("excluded")
            @Schema(description = "제외 여부", example = "false", required = true)
            boolean excluded,

            @JsonProperty("signingMethod")
            @Schema(description = "서명 방법", required = true)
            @NotNull(message = "서명 방법은 null이 될 수 없습니다.")
            SigningMethod signingMethod,

            @JsonProperty("signingDuration")
            @Schema(description = "서명 기간 (분 단위)", example = "20160", required = true)
            @NotNull(message = "서명 기간은 null이 될 수 없습니다.")
            int signingDuration,

            @JsonProperty("locale")
            @Schema(description = "언어", example = "ko", required = true)
            @NotNull(message = "언어는 null이 될 수 없습니다.")
            String locale,

            @JsonProperty("role")
            @Schema(description = "역할", example = "외국인유학생", required = true)
            @NotNull(message = "역할은 null이 될 수 없습니다.")
            String role,

            @JsonProperty("name")
            @Schema(description = "이름", example = "이름1", required = true)
            @NotNull(message = "이름은 null이 될 수 없습니다.")
            String name,

            @JsonProperty("requesterMessage")
            @Schema(description = "요청 메시지", example = "외국인 유학생이 아르바이트를 하기 위해 제출해야 하는 시간제 취업허가서입니다.", required = true)
            @NotNull(message = "요청 메시지는 null이 될 수 없습니다.")
            String requesterMessage
    ) {}

        // 요청자 데이터 입력 매칭
        public record RequesterInputMapping(
                @JsonProperty("dataLabel")
                @Schema(description = "요청자 데이터 라벨 이름", example = "appicantName", required = false)
                String dataLabel,

                @JsonProperty("value")
                @Schema(description = "서명 방법 값", example = "아무말", required = false)
                String value
        ) {}

        // 사인 대상자 매핑
    public record SigningMethod(
            @JsonProperty("type")
            @Schema(description = "서명 방법 타입", example = "EMAIL", required = true)
            @NotNull(message = "서명 방법 타입은 null이 될 수 없습니다.")
            String type,

            @JsonProperty("value")
            @Schema(description = "서명 방법 값", example = "test@gmail.com", required = true)
            @NotNull(message = "서명 방법 값은 null이 될 수 없습니다.")
            String value
    ) {}
}