package com.inglo.giggle.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ScheduleDto(
        Long id,
        String partTimeName,
        Integer hourlyRate,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
