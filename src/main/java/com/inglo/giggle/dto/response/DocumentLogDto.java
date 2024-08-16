package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record DocumentLogDto (
        @JsonProperty("logs")List<DocumentSpec> logs
        ){
    public record DocumentSpec(
            @JsonProperty("name") String name,
            @JsonProperty("date") LocalDate startDate,
            @JsonProperty("step") float step,
            @JsonProperty("stepComment") String stepComment
    ) {

    }
}
