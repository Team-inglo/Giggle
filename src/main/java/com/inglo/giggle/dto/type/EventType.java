package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    // 새로운 문서로 서명 요청이 시작됨
    DOCUMENT_STARTED("document_started"),
    // 참여자가 문서에 서명을 입력함
    DOCUMENT_SIGNED("document_signed"),
    // 문서에 모든 참여자가 서명함
    DOCUMENT_ALL_SIGNED("document_all_signed"),
    // 참여자가 서명 요청을 거절함
    DOCUMENT_REJECTED("document_rejected"),
    // 문서의 서명 요청이 취소됨
    DOCUMENT_REQUEST_CANCELED("document_request_canceled"),
    // 참여자가 입력한 서명을 취소함
    DOCUMENT_SIGNGING_CANCELED("document_signing_canceled");

    private final String type;

    public static EventType fromType(String type) {
        for (EventType eventType : EventType.values()) {
            if (eventType.getType().equals(type)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("No enum constant with type: " + type);
    }
}
