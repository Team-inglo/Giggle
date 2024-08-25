package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(name = "OwnerAnnouncementStatusDetail", description = "고용주가 등록한 아르바이트 공고 상태 상세 조회")
public record OwnerAnnouncementStatusDetailDto(
        @JsonProperty("title") @Schema(description = "공고 제목")
        String title,
        @JsonProperty("addressName") @Schema(description = "일자리 위치")
        String addressName,
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
