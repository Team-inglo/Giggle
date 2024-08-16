package com.inglo.giggle.service;

import com.inglo.giggle.domain.Document;
import com.inglo.giggle.dto.request.ParticipantMappingRequestDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.request.DocumentLogRequestDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.dto.type.EventType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    @Value("${MODUSIGN_API_KEY}")
    private String MODUSIGN_API_KEY;
    @Value("${TEMPLATE_ID_1}")
    private String TEMPLATE_ID_1;
    @Value("${TEMPLATE_ID_2}")
    private String TEMPLATE_ID_2;
    @Value("${TEMPLATE_ID_3}")
    private String TEMPLATE_ID_3;

    private final DocumentRepository documentRepository;
    private final WebClient webClient = WebClient.builder().baseUrl("https://api.modusign.co.kr").build();

    // 시간제 취업허가서 신청
    public String submitDocument(List<ParticipantMappingRequestDto> request, Integer docs_number) {
        List<WebClientRequestDto.ParticipantMapping> participantMappings = request.stream()
                .map(req -> new WebClientRequestDto.ParticipantMapping(
                        false,
                        new WebClientRequestDto.SigningMethod(req.signingMethod().type(), req.signingMethod().value()),
                        20160,
                        "ko",
                        req.role(),
                        req.name(),
                        "외국인 유학생이 아르바이트를 하기 위해 제출해야 하는 시간제 취업허가서입니다."
                ))
                .collect(Collectors.toList());

        WebClientRequestDto.Document document = new WebClientRequestDto.Document(
                participantMappings,
                "시간제취업허가서"
        );

        // docs_number에 따라 TEMPLATE_ID 선택
        String templateId;
        switch (docs_number) {
            case 1: // 시간제 취업허가서
                templateId = TEMPLATE_ID_1;
                break;
            case 2: // 근로 계약서
                templateId = TEMPLATE_ID_2;
                break;
            case 3: // 통합 신청서
                templateId = TEMPLATE_ID_3;
                break;
            default:
                throw new IllegalArgumentException("Invalid docs_number: " + docs_number); // 예외처리 추가
        }

        WebClientRequestDto requestDto = new WebClientRequestDto(
                document,
                templateId // template id 입력
        );

        WebClientResponseDto responseDto = webClient.post()
                .uri("/documents/request-with-template")
                .header("accept", "application/json")
                .header("authorization", MODUSIGN_API_KEY)
                .header("content-type", "application/json")
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status != HttpStatus.CREATED, clientResponse -> {
                    // Handle any status that is not 201 Created
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Unexpected status code: "
                                    + clientResponse.statusCode() + ", body: " + errorBody)));
                })
                .bodyToMono(WebClientResponseDto.class)
                .block();

        return "url";
    }

    // 모두싸인에서 보내는 post용 api
    public void postDocumentLog(DocumentLogRequestDto request) {
        // request에서 document id로 요청 파악
        Document document = documentRepository.findByDocumentId(request.document().id()).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
        EventType eventType = EventType.valueOf(request.event().type()); // eventype get
        handleEvent(eventType, document);
    }

    public void handleEvent(EventType eventType, Document document) {
        switch (eventType) {
            case DOCUMENT_STARTED:
                // 서명 요청 시작
                break;
            case DOCUMENT_SIGNED:
                // 서명에 입력
                break;
            case DOCUMENT_ALL_SIGNED:
                // 문서의 모든 참여자가 서명함.
                document.getApply().addStep(); // step 1 증가
                break;
            case DOCUMENT_REJECTED:
                // 참여자가 서명 요청을 거절함.
                break;
            case DOCUMENT_REQUEST_CANCELED:
                // 문서의 서명 요청이 취소됨.
                break;
            case DOCUMENT_SIGNGING_CANCELED:
                // 참여자가 입력한 서명을 취소함.
                break;
            default:
                throw new CommonException(ErrorCode.INVALID_EVENT_TYPE);
        }
    }

}
