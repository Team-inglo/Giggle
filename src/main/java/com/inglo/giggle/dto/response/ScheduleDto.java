package com.inglo.giggle.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ScheduleDto(
        Long id,
        String partTimeName,
        String partTimeColor,
        Integer hourlyRate,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
