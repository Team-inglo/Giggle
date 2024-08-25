package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EAnnouncementPeriod {
    ALL("ALL", "전체"),
    OPEN("OPEN", "신청중"),
    UPCOMING("OPEN", "신청예정"),
    CLOSED("OPEN", "신청마감");

    private final String status;
    private final String description;
}
