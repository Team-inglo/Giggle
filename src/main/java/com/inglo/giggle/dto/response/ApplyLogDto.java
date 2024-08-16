package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(name = "ApplyLogDto", description = "사용자 작성 서류 상태 확인")
public record ApplyLogDto (
        @JsonProperty("logs")List<DocumentSpec> logs
        ){
    public record DocumentSpec(
            @JsonProperty("name") String name,
            @JsonProperty("date") LocalDate startDate,
            @JsonProperty("step") Integer step,
            @JsonProperty("stepComment") String stepComment
    ) {

    }
}
