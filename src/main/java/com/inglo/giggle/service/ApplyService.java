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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    public UserApplyLogDto getUserApplyLogs(Long userId, Boolean status) {

        // User로 Apply 엔티티 리스트 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<Apply> applies = applyRepository.findByUser(user);

        // status가 동일한 것만 필터링
        List<Apply> filteredApply = applies.stream()
                .filter(apply -> apply.getStatus().equals(status))
                .toList();

        List<UserApplyLogDto.DocumentSpec> documentSpecs = applies.stream()
                .map(apply -> new UserApplyLogDto.DocumentSpec(
                        apply.getAnnouncement().getTitle(),
                        apply.getCreatedAt().toLocalDate(),
                        apply.getStep(),
                        ""
                ))
                .toList();

        return UserApplyLogDto.builder()
                .logs(documentSpecs)
                .build();
    }

    // 신청 서류 디테일 get
    public void getUserApplyDetails(Long userId, Long applyId) {
        Apply apply = applyRepository.findById(applyId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLY));
        // index 몇이 무슨 서류인지 확인 필요
        String documentId = apply.getDocuments().get(0).getDocumentId(); // get docuemnt Id
        String embeddedUrl = getViewEmbeddedUrl(documentId);

        List<UserApplyDetailDto.RemainingStep> remainingSteps = Arrays.stream(PartTimeStep.values())
                .filter(step -> step.getId() > apply.getStep())
                .map(step -> new UserApplyDetailDto.RemainingStep(step.getId().longValue(), step.getComment()))
                .toList();

        UserApplyDetailDto.builder()
                .name(apply.getAnnouncement().getTitle())
                .startDate(apply.getCreatedAt().toLocalDate())
                .step(apply.getStep())
                .documentUrl(embeddedUrl)
                .remainingSteps(remainingSteps)
                .stepComment(RequestStepCommentType.getCommentById(apply.getStep()))
                .build();

    }

    private String getViewEmbeddedUrl(String documentId) {
        String url = String.format("/documents/%s/embedded-view?redirectUrl=%s", documentId, ""); // redirect url 추가

        WebClientEmbeddedResponseDto responseDto = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .header("authorization", MODUSIGN_API_KEY)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, res -> {
                    return res.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new CommonException(ErrorCode.INVALID_MODUSIGN_ERROR)));
                })
                .bodyToMono(WebClientEmbeddedResponseDto.class)
                .block();

        return responseDto.embeddedUrl();
    }

}
