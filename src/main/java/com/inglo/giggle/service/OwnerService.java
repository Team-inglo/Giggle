package com.inglo.giggle.service;

import com.inglo.giggle.domain.*;
import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.request.UpdateOwnerDto;
import com.inglo.giggle.dto.response.OwnerAnnouncementStatusDetailDto;
import com.inglo.giggle.dto.response.OwnerAnnouncementStatusListDto;
import com.inglo.giggle.dto.response.WebClientEmbeddedResponseDto;
import com.inglo.giggle.dto.type.ERequestStepCommentType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final ApplyRepository applyRepository;

    @Value("${MODUSIGN_API_KEY}")
    private String MODUSIGN_API_KEY;

    private final WebClient webClient = WebClient.builder().baseUrl("https://api.modusign.co.kr").build();

    // 아르바이트 공고 생성 post method
    public Long createAnnounement(AnnouncementCreateDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));

        Announcement announcement = Announcement.builder()
                .owner(owner)
                .title(request.title())
                .jobType(request.jobType().name())
                .hourlyRate(request.hourlyWage())
                .workStartDate(request.workStartDate())
                .deadLine(request.deadline())
                .workingPeriod(request.workingPeriod())
                .age(request.age())
                .gender(String.valueOf(request.gender()))
                .education(String.valueOf(request.education()))
                .numberRecruited(request.numberRecruited())
                .content(request.content())
                .build();

        List<WorkDay> workDayList = request.workDays().stream()
                .map(workDay -> WorkDay.builder()
                        .day(workDay.day())
                        .workStartTime(workDay.workStartTime())
                        .workEndTime(workDay.workEndTime())
                        .announcement(announcement)
                        .build())
                .toList();

        announcement.advanceWorkDays(workDayList);
        announcementRepository.save(announcement);

        return announcement.getId();
    }

    // 아르바이트 공고 상태 리스트 조회
    @Transactional
    public OwnerAnnouncementStatusListDto getOwnerAnnouncementStatusList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt"); // 오름차순 정렬

        List<Announcement> announcements = announcementRepository.findByOwner(owner, sort);

        List<OwnerAnnouncementStatusListDto.AnnouncementStatus> announcementDtos = announcements.stream()
                .map(announcement -> {
                    List<Apply> applies = applyRepository.findByAnnouncement(announcement); // 신청 로그 가져오기

                    // applies의 전체 개수
                    int totalApplies = applies.size();

                    // applies 중 status == false 개수
                    long falseStatusApplies = applies.stream()
                            .filter(apply -> !apply.getStatus()) // status가 false인 경우
                            .count();

                    return new OwnerAnnouncementStatusListDto.AnnouncementStatus(
                            announcement.getId(),
                            announcement.getTitle(),
                            announcement.getOwner().getStoreAddressName(),
                            (int) falseStatusApplies,
                            totalApplies,
                            announcement.getDeadLine(),
                            (int) ChronoUnit.DAYS.between(LocalDate.now(), announcement.getDeadLine()) // 날짜 차이를 일수로 계산
                    );
                })
                .toList();

        // 진행 중 apply count
        long progressAnnouncementCount = announcements.stream()
                .filter(announcement -> !applyRepository.findByAnnouncement(announcement).isEmpty()) // apply가 존재하는 announcement 필터링
                .filter(announcement -> announcement.getDeadLine().isAfter(LocalDate.now())) // 마감 기한이 현재 날짜보다 이후인 announcement 필터링
                .count();

        // 종료 서류 count
        long doneAnnouncementCount = announcements.stream()
                .filter(announcement -> !applyRepository.findByAnnouncement(announcement).isEmpty()) // apply가 존재하는 announcement 필터링
                .filter(announcement -> announcement.getDeadLine().isBefore(LocalDate.now())) // 마감 기한이 현재 날짜보다 이전인 announcement 필터링
                .count();

        OwnerAnnouncementStatusListDto ownerAnnouncementStatusListDto = OwnerAnnouncementStatusListDto.builder()
                .progressCompletor((int)progressAnnouncementCount)
                .doneCompletor((int)doneAnnouncementCount)
                .announcementStatuses(announcementDtos)
                .build();

        return ownerAnnouncementStatusListDto;
    }

    // 아르바이트 공소 상태 상세 조회
    @Transactional
    public OwnerAnnouncementStatusDetailDto getOwnerAnnouncementStatusDetails(Long userId, Long announcementId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));

        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ANNOUNCEMENT));

        // Announcement가 해당 Owner의 것인지 확인
        if (!announcement.getOwner().equals(owner)) {
            throw new CommonException(ErrorCode.NOT_OWNERS_ANNOUNCEMENT);
        }

        List<Apply> applies = applyRepository.findByAnnouncement(announcement);

        // applies의 전체 개수(지원자 수)
        int totalApplies = applies.size();

        // applies 중 status == false 개수(서류 완료자 수)
        long falseStatusApplies = applies.stream()
                .filter(apply -> !apply.getStatus()) // status가 false인 경우
                .count();

        // 서류 지원자들 list 정렬
        List<OwnerAnnouncementStatusDetailDto.applicantsStatus> applicantsStatuses = applies.stream()
                .map(apply -> {
                    String embeddedUrl = "";
                    List<Document> documents = apply.getDocuments();

                    // 리스트 크기가 3 이상인지 확인
                    if(!documents.isEmpty() && documents.get(0).getDocumentId() != null) {
                        embeddedUrl = getViewEmbeddedUrl(documents.get(0).getDocumentId());
                    }

                    return new OwnerAnnouncementStatusDetailDto.applicantsStatus(
                            apply.getId(),
                            apply.getApplicant().getName(),
                            apply.getCreatedAt().toLocalDate(),
                            ERequestStepCommentType.getCommentById(apply.getStep()),
                            embeddedUrl // 근로계약서 url. 없는 경우에는 빈 값
                    );
                })
                .toList();

        OwnerAnnouncementStatusDetailDto ownerAnnouncementStatusDetailDto = OwnerAnnouncementStatusDetailDto.builder()
                .title(announcement.getTitle())
                .addressName(announcement.getOwner().getStoreAddressName())
                .completor((int)falseStatusApplies)
                .applicant(totalApplies)
                .dealLine(announcement.getDeadLine())
                .applicantsStatuses(applicantsStatuses)
                .build();

        return ownerAnnouncementStatusDetailDto;
    }

    // 근로계약서 url 호출 api, ApplyService에도 중복되어 있어서 추후 변경이 필요함.
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

    @Transactional
    public void updateOwner(Long userId, UpdateOwnerDto updateOwnerDto) {
        Owner owner = ownerRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
        owner.updateOwner(updateOwnerDto);
    }
}
