package com.inglo.giggle.dto.response;

import com.inglo.giggle.domain.PartTime;
import lombok.Builder;

@Builder
public record PartTimeListDto(
        Long id,
        String name
) {
    public static PartTimeListDto fromEntity(PartTime partTime) {
        return PartTimeListDto.builder()
                .id(partTime.getId())
                .name(partTime.getPartTimeName())
                .build();
    }
}
