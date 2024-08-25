package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EEducation {
    HIGH_SCHOLL_GRADUATION("HIGH_SCHOLL_GRADUATION", "고졸"),
    TWO_YEAR_COLLEGE_GRADUATION("TWO_YEAR_COLLEGE_GRADUATION", "대학 2년제"),
    FOUR_YEAR_COLLEGE_GRADUATION("FOUR_YEAR_COLLEGE_GRADUATION", "대학 4년제"),
    ANY("ANY", "무관");

    private final String type;
    private final String description;

    public static String getDescriptionByType(String type) {
        for (EEducation education : EEducation.values()) {
            if (education.getType().equalsIgnoreCase(type)) {
                return education.getDescription();
            }
        }
        return ""; // 암것도 없는 경우
    }
}
