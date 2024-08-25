package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AnnouncementDetailDto (
        @JsonProperty("title") @Schema(description = "공고 제목")
        String title,

        @JsonProperty("deadLine") @Schema(description = "마감일자")
        LocalDate deadline,

        @JsonProperty("hourlyWage") @Schema(description = "시급")
        Integer hourlyWage,

        @JsonProperty("workStartDate") @Schema(description = "근무 시작일자")
        LocalDate workStartDate,

        @JsonProperty("workEndDate") @Schema(description = "근무 종료일자")
        LocalDate workEndDate,

        @JsonProperty("workDays") @Schema(description = "요일")
        List<String> workDays,

        @JsonProperty("age") @Schema(description = "나이(제한)")
        Integer age,

        @JsonProperty("gender") @Schema(description = "성별(제한)")
        String gender,

        @JsonProperty("workStartTime") @Schema(description = "근무 시작시간")
        LocalTime workStartTime,

        @JsonProperty("workEndTime") @Schema(description = "근무 종료시간")
        LocalTime workEndTime,

        @JsonProperty("education") @Schema(description = "학력")
        String education,

        @JsonProperty("location") @Schema(description = "근무 위치")
        String location,

        @JsonProperty("numberRecruited") @Schema(description = "모집 인원") String numberRecruited,

        @JsonProperty("content") @Schema(description = "상세 요강")
        String content
){
}
