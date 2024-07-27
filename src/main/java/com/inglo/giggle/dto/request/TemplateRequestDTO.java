package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public record TemplateRequestDTO (
) {
    // 시간제 취업허가 서류 request dto
    public record PartTimeWorkEmployee(
            @JsonProperty("myEmail") @Schema(description = "본인이메일", example = "test@gmail.com")
            @NotNull(message = "이메일은 null이 될 수 없습니다.")
            String myEmail,

            @JsonProperty("employerEmail") @Schema(description = "고용주이메일", example = "test@gmail.com")
            @NotNull(message = "이메일은 null이 될 수 없습니다.")
            String employerEmail,

            @JsonProperty("representativeEmail") @Schema(description = "유학생담당자이메일", example = "test@gmail.com")
            @NotNull(message = "이메일은 null이 될 수 없습니다.")
            String representativeEmail
    ) {}
}