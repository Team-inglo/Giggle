package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(name = "ApplyDetail", description = "사용자 작성 서류 상태 상세")
public record UserApplyDetailDto (
        @JsonProperty("name") String name,
        @JsonProperty("date") LocalDate startDate,
        @JsonProperty("step") Integer step,
        @JsonProperty("documentUrl") String documentUrl,
        @JsonProperty("remainingSteps") List<RemainingStep> remainingSteps,
        @JsonProperty("stepComment") String stepComment
        ) {

        public record RemainingStep (
                @JsonProperty("id") Long id,
                @JsonProperty("content") String content
        ) {}
}
