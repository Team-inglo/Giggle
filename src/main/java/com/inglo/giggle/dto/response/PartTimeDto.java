package com.inglo.giggle.dto.response;

import com.inglo.giggle.domain.PartTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "PartTimeDto", description = "아르바이트 조회 Dto")
public record PartTimeDto(
        Long id,
        String name,
        Integer hourlyRate,
        String color
) {
    public static PartTimeDto fromEntity(PartTime partTime) {
        return PartTimeDto.builder()
                .id(partTime.getId())
                .color(partTime.getColor())
                .name(partTime.getPartTimeName())
                .hourlyRate(partTime.getHourlyRate())
                .build();
    }
}
