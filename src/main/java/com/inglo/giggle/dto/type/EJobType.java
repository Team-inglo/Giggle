package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EJobType {
    ANY("전체"),
    FOOD_INDUSTRY("음식업보조"),
    OFFICE("사무"),
    ENGLISH("영어"),
    TOURIST_INFORMATION("관광안내"),
    MANUDACTURING_INDUSTRY("제조업"),
    DAY_WORK("일용근로"),
    INTERNSHIP("인턴");

    private final String type;

    public String getTypeName(EJobType jobType) {
        return jobType.getType();
    }
    public static EJobType fromType(String type) {
        for (EJobType jobType : EJobType.values()) {
            if (jobType.getType().equals(type)) {
                return jobType;
            }
        }
        throw new IllegalArgumentException("No matching EJobType for type: " + type);
    }

}
