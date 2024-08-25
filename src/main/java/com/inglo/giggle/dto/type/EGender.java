package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EGender {
    FEMALE("FEMALE", "여성"),
    MALE("MALE", "남성"),
    ANY("ANY", "무관");

    private final String sex;
    private final String description;

    public static String getDescriptionBySex(String sex) {
        for (EGender gender : EGender.values()) {
            if (gender.getSex().equalsIgnoreCase(sex)) {
                return gender.getDescription();
            }
        }
        return ""; // 암것도 없을 때
    }
}
