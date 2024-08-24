package com.inglo.giggle.service;

import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.Document;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.response.UserApplyDetailDto;
import com.inglo.giggle.dto.response.UserApplyLogDto;
import com.inglo.giggle.dto.response.WebClientEmbeddedResponseDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.dto.type.DocumentType;
import com.inglo.giggle.dto.type.PartTimeStep;
import com.inglo.giggle.dto.type.RequestStepCommentType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.ApplyRepository;
import com.inglo.giggle.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplyService {
    @Value("${MODUSIGN_API_KEY}")
    private String MODUSIGN_API_KEY;

    private final ApplyRepository applyRepository;
    private final UserRepository userRepository;

    private final WebClient webClient = WebClient.builder().baseUrl("https://api.modusign.co.kr").build();

    // document log 조회 api
    @Transactional
    public UserApplyLogDto getUserApplyLogs(Long userId, Boolean status) {

        // User로 Apply 엔티티 리스트 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<Apply> applies = applyRepository.findByUser(user);

        // status가 동일한 것만 필터링
        List<Apply> filteredApply = applies.stream()
                .filter(apply -> apply.getStatus().equals(status))
                .toList();

        List<UserApplyLogDto.DocumentSpec> documentSpecs = filteredApply.stream()
                .map(apply -> new UserApplyLogDto.DocumentSpec(
                        apply.getAnnouncement().getTitle(),
                        apply.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        apply.getStep(),
                        RequestStepCommentType.getCommentById(apply.getStep()) // apply step과 일치하는 stepComment 출력
                ))
                .toList();

        return UserApplyLogDto.builder()
                .logs(documentSpecs)
                .build();
    }

    // 신청 서류 디테일 get
    @Transactional
    public UserApplyDetailDto getUserApplyDetails(Long userId, Long applyId) {
        Apply apply = applyRepository.findById(applyId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLY));

        // Document dto로 변환
        List<UserApplyDetailDto.CompletedDocument> completedDocuments = apply.getDocuments().stream()
                .map(doc -> new UserApplyDetailDto.CompletedDocument(
                        doc.getId(),
                        doc.getType().getTitle(),
                        getViewEmbeddedUrl(doc.getDocumentId())
                ))
                .toList();

        List<UserApplyDetailDto.RemainingStep> remainingSteps = Arrays.stream(PartTimeStep.values())
                .filter(step -> step.getId() > apply.getStep())
                .map(step -> new UserApplyDetailDto.RemainingStep(step.getId().longValue(), step.getComment()))
                .toList();

        UserApplyDetailDto userApplyDetailDto = UserApplyDetailDto.builder()
                .name(apply.getAnnouncement().getTitle())
                .startDate(apply.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .step(apply.getStep())
                .completedDocuments(completedDocuments)
                .remainingSteps(remainingSteps)
                .stepComment(RequestStepCommentType.getCommentById(apply.getStep()))
                .announcementId(apply.getAnnouncement().getId())
                .build();

        return userApplyDetailDto;

    }

    private String getViewEmbeddedUrl(String documentId) {
        String url = String.format("/documents/%s/embedded-view?redirectUrl=%s", documentId, "https://github.com/bianbbc87"); // redirect url 추가

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

}
