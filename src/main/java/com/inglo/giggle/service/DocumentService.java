package com.inglo.giggle.service;

import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.Document;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.request.WebHookRequestDto;
import com.inglo.giggle.dto.response.WebClientEmbeddedResponseDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.dto.type.DocumentType;
import com.inglo.giggle.dto.type.EventType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.AnnouncementRepository;
import com.inglo.giggle.repository.ApplyRepository;
import com.inglo.giggle.repository.DocumentRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    @Value("${MODUSIGN_API_KEY}")
    private String MODUSIGN_API_KEY;

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;
    private final AnnouncementRepository announcementRepository;
    private final WebClient webClient = WebClient.builder().baseUrl("https://api.modusign.co.kr").build();

    // 시간제 취업허가서 신청
    public String requestSinature(List<RequestSignatureDto> request, String documentType, Long announcementId, Long userId) {
        // documentType 가져오기
        DocumentType type = DocumentType.valueOf(documentType);

        List<WebClientRequestDto.ParticipantMapping> participantMappings = request.stream()
                .map(req -> new WebClientRequestDto.ParticipantMapping(
                        false,
                        new WebClientRequestDto.SigningMethod(req.signingMethod().type(), req.signingMethod().value()),
                        20160,
                        "ko",
                        req.role(),
                        req.name(),
                        type.getMessage()
                ))
                .toList();

        WebClientRequestDto.Document document = new WebClientRequestDto.Document(
                participantMappings,
                type.getTitle()
        );

        WebClientRequestDto requestDto = new WebClientRequestDto(
                document,
                type.getTemplateId() // template id
        );

        WebClientResponseDto responseDto = webClient.post()
                .uri("/documents/request-with-template")
                .header("accept", "application/json")
                .header("authorization", MODUSIGN_API_KEY)
                .header("content-type", "application/json")
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status != HttpStatus.CREATED, res -> {
                    return res.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                String errorMessage = String.format("Modusign API error: %s", errorBody);
                                System.out.println(errorMessage);
                                return Mono.error(new CommonException(ErrorCode.INVALID_MODUSIGN_ERROR));
                            });
                })
                .bodyToMono(WebClientResponseDto.class)
                .block();

        String embeddedUrl = getembeddedUrl(responseDto.id(), responseDto.participants().get(0).id());

        addApply(responseDto, announcementId, type, userId);

        return embeddedUrl;
    }

    private String getembeddedUrl(String documentId, String participantId) {
        String url = String.format("/documents/%s/participants/%s/embedded-view?redirectUrl=%s", documentId, participantId, "https://github.com/bianbbc87"); // redirect url 추가

        WebClientEmbeddedResponseDto responseDto = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .header("authorization", MODUSIGN_API_KEY)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, res -> {
                    return res.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                String errorMessage = String.format("Modusign API error: %s", errorBody);
                                System.out.println(errorMessage);
                                return Mono.error(new CommonException(ErrorCode.INVALID_MODUSIGN_ERROR));
                            });
                })
                .bodyToMono(WebClientEmbeddedResponseDto.class)
                .block();

        return responseDto.embeddedUrl();
    }

    private void addApply(WebClientResponseDto request, Long announcementId, DocumentType documentType, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ANNOUNCEMENT));

        Apply apply = Apply.builder()
                .user(user)
                .announcement(announcement)
                .step(1)
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            applyRepository.save(apply);
            // document 추가
            addDocumentForApply(apply, documentType, request.id());

        } catch(DataAccessException e) {
            throw new CommonException(ErrorCode.APPLY_DATABASE_ERROR);
        }
    }

    // apply의 document 추가 method
    private void addDocumentForApply(Apply apply, DocumentType documentType, String documentId) {
        Document document = Document.builder()
                .apply(apply)
                .type(documentType)
                .documentId(documentId)
                .build();

        try {
            documentRepository.save(document);
        } catch (DataAccessException e) {
            throw new CommonException(ErrorCode.DOCUMENT_DATABASE_ERROR);
        }
    }

    // 모두싸인에서 보내는 post용 api
    public void requestWebHook(WebHookRequestDto request) {
        // request에서 document id로 요청 파악
        Document document = documentRepository.findByDocumentId(request.document().id()).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
        EventType eventType = EventType.valueOf(request.event().type()); // eventype get
        handleEvent(eventType, document);
    }

    private void handleEvent(EventType eventType, Document document) {
        switch (eventType) {
            case DOCUMENT_STARTED:
                // 서명 요청 시작
                break;
            case DOCUMENT_SIGNED:
                // 서명에 입력
                break;
            case DOCUMENT_ALL_SIGNED:
                // 문서의 모든 참여자가 서명함.
                Apply apply = document.getApply();
                apply.addStep(); // step 1 증가

                if (apply.getStep() >= 8) {
                    // 단계가 완료된 경우 status를 false로
                    apply.advanceStatus();
                }

                applyRepository.save(apply);
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
