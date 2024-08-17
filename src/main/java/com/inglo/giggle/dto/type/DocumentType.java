package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    TIME_WORK_PERMIT("", "시간제취업허가서", "외국인 유학생이 아르바이트를 하기 위해 제출해야 하는 시간제 취업허가서입니다."), // templateId 1
    EMPLOYMENT_CONTRACT("", "근로계약서", "상호 간 고용 계약을 위한 근로 계약서입니다."), // templateId 2
    INTEGRATED_APPLICATION("", "통합신청서", "외국인 유학생이 행정 업무를 신청하기 위한 통합 신청서입니다."); // templateId 3

    private final String templateId;
    private final String title;
    private final String message;

    public String getMessage(DocumentType documentType) {
        return documentType.getMessage();
    }

}
