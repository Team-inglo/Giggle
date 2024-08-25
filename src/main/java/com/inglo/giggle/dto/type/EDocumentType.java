package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDocumentType {

    TIME_WORK_PERMIT("173d10b0-46a9-11ef-82c8-6b94a46e1b38", "시간제취업허가서", "외국인 유학생이 아르바이트를 하기 위해 제출해야 하는 시간제 취업허가서입니다."), // templateId 1
    EMPLOYMENT_CONTRACT("c7d3b5e0-46aa-11ef-82c8-6b94a46e1b38", "표준 근로계약서", "상호 간 고용 계약을 위한 근로 계약서입니다."), // templateId 2
    INTEGRATED_APPLICATION("4693e360-46a5-11ef-ab36-79d1aa0a792e", "통합신청서", "외국인 유학생이 행정 업무를 신청하기 위한 통합 신청서입니다."); // templateId 3

    private final String templateId;
    private final String title;
    private final String message;

    public String getMessage(EDocumentType documentType) {
        return documentType.getMessage();
    }
}
