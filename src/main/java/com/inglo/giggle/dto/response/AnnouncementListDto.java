package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(name = "AnnouncementList", description = "아르바이트 공고 리스트 조회 response dto")
public record AnnouncementListDto(
        @JsonProperty("tags") @Schema(description = "선택한 태그") List<Tag> selectedTags,

        @JsonProperty("announcements") @Schema(description = "아르바이트 공고들") List<Announcement> announcements

) {

    public record Tag(
            @JsonProperty("id") @Schema(description = "tag id") Long id,
            @JsonProperty("category") @Schema(description = "tag 이름") String category
    ) {

    }

    public record Announcement(
            @JsonProperty("id") @Schema(description = "announcement id") Long id,
            @JsonProperty("deadlineDDay") @Schema(description = "마감까지 남은 일자") Integer restOfDay,
            @JsonProperty("hourlyWage") @Schema(description = "시급") Integer hourlyWage,
            @JsonProperty("title") @Schema(description = "아르바이트 제목") String title,
            @JsonProperty("location") @Schema(description = "아르바이트 주소") String location,
            @JsonProperty("numberRecruited") @Schema(description = "모집 인원") Integer numberRecruited
    ) {

    }
}
