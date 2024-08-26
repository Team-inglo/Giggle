package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SejongInstituteUpdateDto(
        @JsonProperty("sejong_institute_score")
        String sejongInstituteScore
) {
}
