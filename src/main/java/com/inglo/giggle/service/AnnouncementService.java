package com.inglo.giggle.service;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

}
