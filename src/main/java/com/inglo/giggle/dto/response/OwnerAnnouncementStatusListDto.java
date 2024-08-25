package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(name = "OwnerAnnouncementStatusList", description = "고용주가 등록한 아르바이트 공고 상태 리스트 조회")
public record OwnerAnnouncementStatusListDto(
        @JsonProperty("progressCompletor") @Schema(description = "진행 중 건수")
        Integer progressCompletor,
        @JsonProperty("doneCompletor") @Schema(description = "지원 완료 건수")
        Integer doneCompletor,
        @JsonProperty("announcementStatuses") @Schema(description = "공고 상태들 배열")
        List<AnnouncementStatus> announcementStatuses


) {
    public record AnnouncementStatus(

            @JsonProperty("announcementId") @Schema(description = "공고 id")
            Long announcementId,

            @JsonProperty("title") @Schema(description = "제목")
            String title,

            @JsonProperty("addressName") @Schema(description = "위치")
            String addressName,

            @JsonProperty("completor") @Schema(description = "서류완료자 수")
            Integer completor,

            @JsonProperty("applicant") @Schema(description = "지원자수")
            Integer applicant,

            @JsonProperty("deadLine") @Schema(description = "마감 일자") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate deadline, // yyyy-mm-dd

            @JsonProperty("numberRecruited") @Schema(description = "마감까지 남은 일자")
            Integer numberRecruited
    ) {

    }
}
