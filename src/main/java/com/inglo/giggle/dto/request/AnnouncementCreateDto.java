package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @JsonProperty("deadLine") @Schema(description = "마감일자", required = true) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate deadline,

        @JsonProperty("hourlyWage") @Schema(description = "시급", required = true)
        Integer hourlyWage,

        @JsonProperty("workStartDate") @Schema(description = "근무 시작일자", required = true) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate workStartDate,

        @JsonProperty("workingPeriod") @Schema(description = "근무 기간(개월 단위)", required = true)
        Integer workingPeriod,

        @JsonProperty("workDays") @Schema(description = "요일", required = true)
        List<workDay> workDays,

        @JsonProperty("age") @Schema(description = "나이(제한)", required = true)
        Integer age,

        @JsonProperty("gender") @Schema(description = "성별(제한)", required = true)
        String gender,

        @JsonProperty("education") @Schema(description = "학력", required = true)
        String education,

        @JsonProperty("location") @Schema(description = "근무 위치 풀네임", required = true)
        String location,

        @JsonProperty("x") @Schema(description = "근무 위치 x 좌표", required = true)
        float x,

        @JsonProperty("location") @Schema(description = "근무 위치 y 좌표", required = true)
        float y,

        @JsonProperty("numberRecruited") @Schema(description = "모집 인원", required = true)
        String numberRecruited,

        @JsonProperty("content") @Schema(description = "상세 요강", required = true)
        String content
) {

        public record workDay(
                @JsonProperty("id") @Schema(description = "요일 데이터 id", required = true)
                Long id,

                @JsonProperty("day") @Schema(description = "요일", required = true)
                String day,
                @JsonProperty("workStartTime") @Schema(description = "근무 시작시간", required = true) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
                LocalTime workStartTime,

                @JsonProperty("workEndTime") @Schema(description = "근무 종료시간", required = true) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
                LocalTime workEndTime
        ) {

        }

}
