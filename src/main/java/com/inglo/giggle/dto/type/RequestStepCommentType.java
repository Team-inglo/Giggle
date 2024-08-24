package com.inglo.giggle.dto.type;

import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStepCommentType {
    ME_WRITE_LABOR_CONTRACT(1, "고용주가 표준 근로계약서를 확인 중이에요."),
    EMPLOYER_WRITE_LABOR_CONTRACT(2, "고용주가 표준 근로계약서를 작성 완료했어요. 시간제 취업허가서를 작성해주세요."),
    ME_WRITE_PARTTIME_PERMIT(3, "고용주가 시간제 취업허가서를 확인 중이에요."),
    EMPLOYER_WRITE_PARTTIME_PERMIT(4, "유학생 담당자가 시간제 취업허가서를 확인 중이에요."),
    COLLAGE_WRITE_PARTTIME_PERMIT(5, "고용주와 유학생 담당자가 시간제 취업허가서 작성을 완료했어요. 통합신청서를 작성해주세요."),
    ME_WRITE_CONSOLIDATION(6, "모든 서류 작성을 완료했습니다. 서류 내용을 검토한 후 전자민원을 신청해주세요."),

    // error code
    EMPLOYER_CANCLE_LABOR_CONSOLIDATION(100, "고용주가 근로계약서 작성을 취소했어요."),
    EMPLOYER_CANCLE_PARTTIME_PERMIT(99, "고용주가 시간제 취업허가서 작성을 취소했어요."),
    COLLAGE_CANCLE_PARTTIME_PERMIT(98, "유학생담당자가 시간제 취업허가서 작성을 취소했어요.");

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
