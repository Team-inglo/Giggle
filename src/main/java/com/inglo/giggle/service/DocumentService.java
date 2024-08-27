package com.inglo.giggle.service;

import com.inglo.giggle.domain.*;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.request.WebHookRequestDto;
import com.inglo.giggle.dto.response.WebClientEmbeddedResponseDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.dto.type.EDocumentType;
import com.inglo.giggle.dto.type.ERequestStepCommentType;
import com.inglo.giggle.dto.type.EventType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static com.inglo.giggle.dto.type.EDocumentType.TIME_WORK_PERMIT;

@Service
@RequiredArgsConstructor
public class DocumentService {
    @Value("${MODUSIGN_API_KEY}")
    private String MODUSIGN_API_KEY;

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;
    private final AnnouncementRepository announcementRepository;
    private final ApplicantRepository applicantRepository;

    private final WebClient webClient = WebClient.builder().baseUrl("https://api.modusign.co.kr").build();

    // 시간제 취업허가서 신청
    @Transactional
    public String requestSignature(List<RequestSignatureDto> request, String documentType, Long announcementId, Long userId) {
        // documentType 가져오기
        EDocumentType type = EDocumentType.valueOf(documentType);

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Applicant applicant = applicantRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ANNOUNCEMENT));

        // 외국인유학생, 고용주의 이름
        String ownerName = announcement.getOwner().getOwnerName();
        String applicantName = applicant.getName();

        List<WebClientRequestDto.ParticipantMapping> participantMappings = request.stream()
                .map(req -> {
                    String name;
                    if ("고용주".equals(req.role())) {
                        name = ownerName;
                    } else if ("외국인유학생".equals(req.role())) {
                        name = applicantName;
                    } else {
                        name = req.name(); // 기본 값
                    }

                    return new WebClientRequestDto.ParticipantMapping(
                            false,
                            new WebClientRequestDto.SigningMethod(req.signingMethod().type(), req.signingMethod().value()),
                            20160,
                            "ko",
                            req.role(),
                            name,  // name을 위에서 설정한 값으로 대체
                            type.getMessage()
                    );
                })
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

        if (documentType.equals("EMPLOYMENT_CONTRACT")) {
            // EMPLOYMENT_CONTRACT인 경우, 새로운 apply 객체 생성
            addApply(responseDto, announcementId, type, userId, applicant, announcement);
        } else {
            // TIME_WORK_PERMIT 또는 INTEGRATED_APPLICATION인 경우, 기존 apply 객체 찾아서 업데이트
            updateOrAddToExistingApply(responseDto, announcementId, type, userId, applicant, announcement);
        }


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

    private void addApply(WebClientResponseDto request, Long announcementId, EDocumentType documentType, Long userId, Applicant applicant, Announcement announcement) {

        Apply apply = Apply.builder()
                .applicant(applicant)
                .announcement(announcement)
                .step(0)
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            applyRepository.save(apply);
            // document 추가
            addDocumentForApply(apply, documentType, request.id(), request.participants().get(1).signingMethod().value(), "");

        } catch(DataAccessException e) {
            throw new CommonException(ErrorCode.APPLY_DATABASE_ERROR);
        }
    }

    // 기존 apply에 documents 객체 추가(시간제취업허가서, 통합신청서의 경우)
    private void updateOrAddToExistingApply(WebClientResponseDto request, Long announcementId, EDocumentType documentType, Long userId, Applicant applicant, Announcement announcement) {
        Apply apply = applyRepository.findByAnnouncementIdAndApplicantId(announcementId, applicant.getId()).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLY));

        if (apply == null) {
            // Apply 객체가 존재하지 않으면 새로운 Apply 생성
            addApply(request, announcementId, documentType, userId, applicant, announcement);
        } else {
            // 기존 Apply 객체에 문서 추가
            try {
                if (documentType.equals(TIME_WORK_PERMIT)) {
                    addDocumentForApply(apply, documentType, request.id(), request.participants().get(1).signingMethod().value(), request.participants().get(2).signingMethod().value());
                } else {
                    addDocumentForApply(apply, documentType, request.id(), "", "");
                }
                applyRepository.save(apply);
            } catch (DataAccessException e) {
                throw new CommonException(ErrorCode.APPLY_DATABASE_ERROR);
            }
        }
    }

    // apply의 document 추가 method
    private void addDocumentForApply(Apply apply, EDocumentType documentType, String documentId, String employerMethod, String staffMethod) {
        Document document = Document.builder()
                .apply(apply)
                .type(documentType)
                .documentId(documentId)
                .employerMethod(employerMethod)
                .staffMethod(staffMethod)
                .build();

        try {
            documentRepository.save(document);
        } catch (DataAccessException e) {
            throw new CommonException(ErrorCode.DOCUMENT_DATABASE_ERROR);
        }
    }

    // 모두싸인에서 보내는 post용 api
    @Transactional
    public void requestWebHook(WebHookRequestDto request) {
        // request에서 document id로 요청 파악
        handleEvent(request);
    }

    private void handleEvent(WebHookRequestDto request) {
        Document document = documentRepository.findByDocumentId(request.document().id()).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
        EventType eventType = EventType.fromType(request.event().type()); // eventype get
        Apply apply = document.getApply();

        String requesterMethod = request.document().requester().email(); // request 요청 당사자 이메일
        String employerMethod = document.getEmployerMethod(); // 등록된 고용주 이메일
        String staffMethod = document.getStaffMethod(); // 등록된 교내유학생담당자 이메일

        switch (eventType) {
            case DOCUMENT_STARTED:
                // 서명 요청 시작
                System.out.println("DOCUMENT_STARTED");
                break;
            case DOCUMENT_SIGNED:
                // 서명에 입력
                System.out.println("DOCUMENT_SIGNED");
                apply.addStep(); // step 1 증가

                if (apply.getStep() >= 6) {
                    // 단계가 완료된 경우 status를 false로
                    apply.advanceStatus();
                }

                // FCM 관련
                if(requesterMethod.equals(employerMethod)) {
                    // 고용주 알림 전송, 메시지는 apply의 step과 연결된 메시지
                    System.out.println(ERequestStepCommentType.getCommentById(apply.getStep()));
                }

                if(requesterMethod.equals(staffMethod)) {
                    // 교내유학생담당자 알림 전송, 메시지는 apply의 step과 연결된 메시지
                    System.out.println(ERequestStepCommentType.getCommentById(apply.getStep()));
                }


                applyRepository.save(apply);

                break;
            case DOCUMENT_ALL_SIGNED:
                // 문서의 모든 참여자가 서명함.
                System.out.println("DOCUMENT_ALL_SIGNED");
                break;
            case DOCUMENT_REJECTED:
                System.out.println("DOCUMENT_REJECTED");
                // 참여자가 서명 요청을 거절함.
                break;
            case DOCUMENT_REQUEST_CANCELED:
                System.out.println("DOCUMENT_SIGNED");
                // 문서의 서명 요청이 취소됨.
                break;
            case DOCUMENT_SIGNGING_CANCELED:
                System.out.println("DOCUMENT_SIGNGING_CANCELED");
                // 참여자가 입력한 서명을 취소함.
                break;
            default:
                throw new CommonException(ErrorCode.INVALID_EVENT_TYPE);
        }
    }

}
