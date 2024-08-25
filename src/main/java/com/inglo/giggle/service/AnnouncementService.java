package com.inglo.giggle.service;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.domain.WorkDay;
import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.response.AnnouncementDetailDto;
import com.inglo.giggle.dto.response.AnnouncementListDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.dto.type.EAnnouncementPeriod;
import com.inglo.giggle.dto.type.EDocumentType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.AnnouncementRepository;
import com.inglo.giggle.repository.OwnerRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

    // 아르바이트 공고 생성
    public AnnouncementListDto getAnnouncementListForCategory(Long userId, String sortBy, Boolean isOwner, String jobType, String sortOrder, List<String> region, EAnnouncementPeriod period) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
        List<Announcement> announcements;

        // 정렬 객체 생성
        Sort sort = "DESC".equalsIgnoreCase(sortOrder) ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();

        if(isOwner != null && isOwner) { // 고용주라면
            announcements = announcementRepository.findByOwner(owner, sort); // 고용주거 중에서 정렬
        } else { // 고용주가 아니라면
            announcements = announcementRepository.findAll(sort); // 그냥 정렬
        }

        if(sortBy != null && sortBy.equals("RECOMMENDATION")) { // 추천 알고리즘이라면
            announcements = filterdSortByRecommendation(sortBy, announcements); // 추천 알고리즘 추가 필요
        } else {
            // 변화 없음.
        }

        if(jobType != null) { // jobType이 있다면
            announcements = filterdJobType(jobType, announcements);
        } else {
            // 변화 없음.
        }

        if(region != null && !region.isEmpty()) { // region이 있다면
            announcements = filterdRegion(region, announcements);
        } else {
            // 변화 없음.
        }

        if(period != null && period != EAnnouncementPeriod.ALL) { // 공고 진행 상태가 있다면
            announcements = filterdPeriod(period, announcements);
        } else {
            // 변화 없음.
        }

        // AnnouncementListDto로 매핑
        List<AnnouncementListDto.Announcement> announcementDtos = announcements.stream()
                .map(announcement -> new AnnouncementListDto.Announcement(
                        announcement.getId(),
                        (int) ChronoUnit.DAYS.between(LocalDate.now(), announcement.getDeadLine()), // 날짜 차이를 일수로 계산
                        announcement.getHourlyRate(),
                        announcement.getTitle(),
                        announcement.getOwner().getStoreAddressName(),
                        announcement.getNumberRecruited()
                ))
                .toList();

        AnnouncementListDto announcementListDto = AnnouncementListDto.builder()
                .announcements(announcementDtos)
                .build();

        return announcementListDto;
    }

    // 유학생 - 추천 알고리즘 사용
    public List<Announcement> filterdSortByRecommendation(String sortBy, List<Announcement> announcements) {
        // 로직 추가 필요
        return announcements;
    }

    // 전체 - 지역으로 분류
    public List<Announcement> filterdRegion(List<String> region, List<Announcement> announcements) {
        // 로직 추가 필요
        return announcements;
    }

    // 전체 - 공고 모집 상태로 분류
    public List<Announcement> filterdPeriod(EAnnouncementPeriod period, List<Announcement> announcements) {
        LocalDate today = LocalDate.now();

        announcements = announcements.stream()
                .filter(announcement -> {
                    LocalDate deadLine = announcement.getDeadLine();

                    // 날짜 구분에 따른 필터링
                    return switch (period) {
                        case OPEN -> deadLine.isAfter(today); // 모집 중인 경우 (현재 날짜보다 이후인 경우)
                        case CLOSED -> deadLine.isBefore(today); // 모집 마감인 경우 (현재 날짜보다 이전인 경우)
                        default -> true; // 기본 처리 로직 (모든 공고 포함)
                    };
                })
                .toList();

        return announcements;
    }

    // 전체 - 업직종으로 분류
    public List<Announcement> filterdJobType(String jobType, List<Announcement> announcements) {
        announcements = announcements.stream()
                .filter(announcement -> announcement.getJobType().equals(jobType))
                .toList();

        return announcements;
    }

    public AnnouncementDetailDto getAnnouncementDetails(Long userId, Long announcementId) {
        userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ANNOUNCEMENT));

        // 요일을 시작 시간과 종료 시간으로 그룹화
        Map<String, List<DayOfWeek>> groupedByTime = announcement.getWorkDays().stream()
                .collect(Collectors.groupingBy(
                        workDay -> workDay.getWorkStartTime().toString() + "-" + workDay.getWorkEndTime().toString(),
                        LinkedHashMap::new,  // 순서를 유지하기 위해 LinkedHashMap 사용
                        Collectors.mapping(WorkDay::getDay, Collectors.toList())
                ));

        // 요일을 ','로, 시간 그룹을 '/'로 구분하여 문자열 생성
        String workDaysString = groupedByTime.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .map(day -> day.getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                        .collect(Collectors.joining(","))
                )
                .collect(Collectors.joining("/"));

        // 시간 그룹을 '/'로 구분하여 문자열 생성
        String workTimesString = String.join("/", groupedByTime.keySet());

        AnnouncementDetailDto announcementDetailDto = AnnouncementDetailDto.builder()
                .title(announcement.getTitle())
                .deadline(announcement.getDeadLine())
                .hourlyWage(announcement.getHourlyRate())
                .workStartDate(announcement.getWorkStartDate())
                .workingPeriod(announcement.getWorkingPeriod())
                .workDays(workDaysString)  // 요일 정보
                .workTimes(workTimesString)  // 시간 정보
                .age(announcement.getAge())
                .gender(announcement.getGender())
                .education(announcement.getEducation())
                .addressName(announcement.getOwner().getStoreAddressName())
                .numberRecruited(announcement.getNumberRecruited())
                .content(announcement.getContent())
                .build();

        return announcementDetailDto;
    }
}
