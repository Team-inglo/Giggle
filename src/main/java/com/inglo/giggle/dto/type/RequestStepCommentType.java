package com.inglo.giggle.dto.type;

import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStepCommentType {
    ME_WRITE_LABOR_CONTRACT(1, "표준 근로계약서 작성"),
    EMPLOYER_WRITE_LABOR_CONTRACT(2, "고용주의 표준 근로계약서 작성"),
    ME_WRITE_PARTTIME_PERMIT(3, "시간제 취업허가서 작성"),
    EMPLOYER_WRITE_PARTTIME_PERMIT(4, "고용주의 시간제 취업허가서 작성"),
    COLLAGE_WRITE_PARTTIME_PERMIT(5, "유학생 담당자의 시간제 취업허가서 작성"),
    ME_WRITE_CONSOLIDATION(6, "통합 신청서 작성"),
    REVIEW_CREATION(7, "작성 내용 검토"),
    HI_KOREA_COMPLAINT(8, "하이코리아 전자민원 신청");


    private final Integer step;
    private final String comment;

    public static String getCommentById(Integer id) {
        for (RequestStepCommentType step : values()) {
            if (step.getStep().equals(id)) {
                return step.getComment();
            }
        }
        throw new CommonException(ErrorCode.INVALID_PARTTIME_STEP);
    }
}
