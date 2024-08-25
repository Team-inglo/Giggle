package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AnnouncementListDto(
        @JsonProperty("tags") @Schema(description = "선택한 태그") List<String> selectedTags,

        @JsonProperty("announcements") @Schema(description = "아르바이트 공고들") List<Announcement> announcements

) {
    public record Announcement(
            @JsonProperty("deadlineDDay") @Schema(description = "마감까지 남은 일자") Integer deadlineDDay,
            @JsonProperty("hourlyWage") @Schema(description = "시급") Integer hourlyWage,
            @JsonProperty("title") @Schema(description = "아르바이트 제목") String title,
            @JsonProperty("location") @Schema(description = "아르바이트 주소") String location,
            @JsonProperty("numberRecruited") @Schema(description = "모집 인원") Integer numberRecruited,

            @JsonProperty("isInterested") @Schema(description = "좋아요 여부") Boolean isInterested
    ) {

    }
}
