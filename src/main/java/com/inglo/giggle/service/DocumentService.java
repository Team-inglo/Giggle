package com.inglo.giggle.service;

import com.inglo.giggle.dto.request.ParticipantMappingRequestDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
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
    public WebClientResponseDto submitDocument(List<ParticipantMappingRequestDto> request, Integer docs_number) {
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

        return responseDto;
    }
}
