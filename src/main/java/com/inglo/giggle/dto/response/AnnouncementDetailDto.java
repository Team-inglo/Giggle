package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@Schema(name = "AnnouncementDetail", description = "아르바이트 공고 상세 조회 response dto")
public record AnnouncementDetailDto (
        @JsonProperty("title") @Schema(description = "공고 제목")
        String title,

        @JsonProperty("deadLine") @Schema(description = "마감일자") // yyyy-mm-dd
        String deadline,

        @JsonProperty("hourlyWage") @Schema(description = "시급")
        Integer hourlyWage,

        @JsonProperty("workStartDate") @Schema(description = "근무 시작일자") // yyyy-mm-dd
        String workStartDate,

        @JsonProperty("workingPeriod") @Schema(description = "근무 기간") // 근무 기간
        Integer workingPeriod,

        @JsonProperty("workDays") @Schema(description = "근무 요일")
        String workDays,

        @JsonProperty("workTime") @Schema(description = "근무 시간")
        String workTimes,

        @JsonProperty("age") @Schema(description = "나이(제한)")
        Integer age,

        @JsonProperty("gender") @Schema(description = "성별(제한)")
        String gender,

        @JsonProperty("education") @Schema(description = "학력")
        String education,

        @JsonProperty("location") @Schema(description = "근무 위치")
        String location,

        @JsonProperty("numberRecruited") @Schema(description = "모집 인원")
        String numberRecruited,

        @JsonProperty("content") @Schema(description = "상세 요강")
        String content
){
}
