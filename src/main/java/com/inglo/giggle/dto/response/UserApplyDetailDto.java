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
        @JsonProperty("startDate") String startDate,
        @JsonProperty("step") Integer step,
        @JsonProperty("completedDocuments") List<CompletedDocument> completedDocuments,
        @JsonProperty("remainingSteps") List<RemainingStep> remainingSteps,
        @JsonProperty("stepComment") String stepComment,
        @JsonProperty("announcementId") Long announcementId
        ) {

        public record CompletedDocument (
                @JsonProperty("id") Long id,
                @JsonProperty("name") String name,
                @JsonProperty("url") String url

        ) {}

        public record RemainingStep (
                @JsonProperty("id") Long id,
                @JsonProperty("content") String content
        ) {}

}
