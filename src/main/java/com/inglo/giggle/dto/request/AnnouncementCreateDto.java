package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@Schema(name = "AnnouncementCreateDto", description = "아르바이트 공고 생성 request dto")
public record AnnouncementCreateDto(
        @JsonProperty("title") @Schema(description = "공고 제목", required = true)
        String title,

        @JsonProperty("deadLine") @Schema(description = "마감일자", required = true)
        LocalDate deadline,

        @JsonProperty("hourlyWage") @Schema(description = "시급", required = true)
        Integer hourlyWage,

        @JsonProperty("workStartDate") @Schema(description = "근무 시작일자", required = true)
        LocalDate workStartDate,

        @JsonProperty("workEndDate") @Schema(description = "근무 종료일자", required = true)
        LocalDate workEndDate,

        @JsonProperty("workDays") @Schema(description = "요일", required = true)
        List<String> workDays,

        @JsonProperty("age") @Schema(description = "나이(제한)", required = true)
        Integer age,

        @JsonProperty("gender") @Schema(description = "변경(제한)", required = true)
        String gender,

        @JsonProperty("workStartTime") @Schema(description = "근무 시작시간", required = true)
        LocalTime workStartTime,

        @JsonProperty("workEndTime") @Schema(description = "근무 종료시간", required = true)
        LocalTime workEndTime,

        @JsonProperty("education") @Schema(description = "교육", required = true)
        String education,

        @JsonProperty("location") @Schema(description = "근무 위치", required = true)
        String location
) {

}
