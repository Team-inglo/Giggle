package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OwnerAnnouncementStatusDetailDto(
        @JsonProperty("title") @Schema(description = "공고 제목")
        String title,
        @JsonProperty("location") @Schema(description = "일자리 위치")
        String location,
        @JsonProperty("completor") @Schema(description = "서류 완료자 수")
        Integer completor,
        @JsonProperty("applicant") @Schema(description = "지원자 수")
        Integer applicant,
        @JsonProperty("dealLine") @Schema(description = "모집 마감일")
        String dealLine,
        @JsonProperty("applicantsStatuses") @Schema(description = "지원자 상태 배열")
        List<applicantsStatus> applicantsStatuses
) {
    public record applicantsStatus(
            @JsonProperty("id") @Schema(description = "id")
            Long id,
            @JsonProperty("name") @Schema(description = "지원자 이름")
            String name,
            @JsonProperty("dateOfApplication") @Schema(description = "지원 날짜")
            String dateOfApplication, // yyyy-mm-dd
            @JsonProperty("statusComment") @Schema(description = "상태 메시지")
            String statusComment,
            @JsonProperty("employmentContractUrl") @Schema(description = "근로계약서 url")
            String employmentContractUrl
    ) {

    }
}
