package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record OwnerAnnouncementStatusListDto(
        @JsonProperty("progressCompletor") @Schema(description = "진행 중 건수")
        Integer progressCompletor,
        @JsonProperty("doneCompletor") @Schema(description = "지원 완료 건수")
        Integer doneCompletor,
        @JsonProperty("announcementStatuses") @Schema(description = "공고 상태들 배열")
        List<AnnouncementStatus> announcementStatuses


) {
    public record AnnouncementStatus(

            @JsonProperty("id") @Schema(description = "id")
            Long id,

            @JsonProperty("title") @Schema(description = "제목")
            String title,

            @JsonProperty("location") @Schema(description = "위치")
            String location,

            @JsonProperty("completor") @Schema(description = "서류완료자 수")
            Integer completor,

            @JsonProperty("applicant") @Schema(description = "지원자수")
            Integer applicant,

            @JsonProperty("deadLine") @Schema(description = "마감 일자")
            String deadline, // yyyy-mm-dd

            @JsonProperty("numberRecruited") @Schema(description = "마감까지 남은 일자")
            Integer numberRecruited
    ) {

    }
}
