package com.inglo.giggle.service;

import com.inglo.giggle.domain.*;
import com.inglo.giggle.dto.response.AnnouncementDetailDto;
import com.inglo.giggle.dto.response.AnnouncementListDto;
import com.inglo.giggle.dto.type.*;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.AnnouncementRepository;
import com.inglo.giggle.repository.ApplicantRepository;
import com.inglo.giggle.repository.OwnerRepository;
import com.inglo.giggle.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

    // 아르바이트 공고 생성
    @Transactional
    public AnnouncementListDto getAnnouncementListForCategory(Long userId, String sortBy, Boolean isOwner, String jobType, String sortOrder, List<String> region, EAnnouncementPeriod period) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<Announcement> announcements;

        // 정렬 객체 생성
        Sort sort = "DESC".equalsIgnoreCase(sortOrder) ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();

        if(isOwner != null && isOwner) { // 고용주라면
            Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
            announcements = announcementRepository.findByOwner(owner, sort); // 고용주거 중에서 정렬
        } else { // 고용주가 아니라면
            announcements = announcementRepository.findAll(sort); // 그냥 정렬
        }

        if(sortBy != null && sortBy.equals("RECOMMENDATION")) { // 추천 알고리즘이라면
            Applicant applicant = applicantRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
            announcements = filterdSortByRecommendation(applicant, sortBy, announcements); // 추천 알고리즘 추가 필요
        } else {
            // 변화 없음.
        }

        if(jobType != null && !jobType.equals("ANY")) { // jobType이 있다면
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
    public List<Announcement> filterdSortByRecommendation(Applicant applicant, String sortBy, List<Announcement> announcements) {
        // 거리 계산
        double maxDistance = 75.0; // 1시간 30분 이내 거리, 75km
        announcements = announcementRepository.findByOwnerLocationWithin((double)applicant.getAddressX(), (double)applicant.getAddressY(), maxDistance);

        final int workingHours = calculateWorkingHours(applicant); // 조건에 따른 근로 가능 시간 조회

        // 가능한 근로 시간 이상을 가진 아르바이트만을 가져오기
        System.out.println(workingHours);
        announcements = announcements.stream()
                .filter(announcement -> calculateTotalWorkHours(announcement) <= workingHours)
                .toList();

        // 업직종 계산
        if(Integer.parseInt(applicant.getTopikScore()) < 4) {
            // 제조업 제외 아르바이트들만 조회(제조업의 경우 TOPIK 4급 이상만 가능) - 단, 사업자등록장에 등록된 제조업은 제외
            announcements = announcements.stream()
                    .filter(announcement -> !Objects.equals(announcement.getJobType(), EJobType.MANUDACTURING_INDUSTRY.name()))
                    .toList();
        }

        return announcements;
    }

    // 조건에 따른 근로 가능 시간 조회
    private int calculateWorkingHours(Applicant applicant) {
        int workingHours = 0;

        // 1 ~ 2학년 기본 근로시간 설정
        if (applicant.getSemester() <= 4) { // 1~2학년 조건
            if (Integer.parseInt(applicant.getTopikScore()) > 2
                    && Integer.parseInt(applicant.getSocialIntegrationProgramScore()) > 2
                    && Integer.parseInt(applicant.getSejongInstituteScore()) > 2) { // 세종학당 중급1 점수
                if (Float.parseFloat(applicant.getGpa()) > 3.49) { // 우수 장학생
                    workingHours = 30;
                } else {
                    workingHours = 25;
                }
            } else {
                workingHours = 10; // 1~2학년 조건이 안 되는 경우 기본 10시간
            }
        }

        // 3~4학년 조건
        if (applicant.getSemester() > 4 && applicant.getSemester() <= 8) { // 3~4학년
            if (Integer.parseInt(applicant.getTopikScore()) > 3
                    && Integer.parseInt(applicant.getSocialIntegrationProgramScore()) > 3
                    && Integer.parseInt(applicant.getSejongInstituteScore()) > 3) { // 세종학당 중급2 점수
                if (Float.parseFloat(applicant.getGpa()) > 3.49) { // 우수 장학생
                    workingHours = 30;
                } else {
                    workingHours = 25;
                }
            } else {
                workingHours = 10; // 조건 미달 시 10시간
            }
        }

        // 석사 이상 조건
        if (applicant.getSemester() > 8) { // 석사 이상
            if (Integer.parseInt(applicant.getTopikScore()) > 3
                    && Integer.parseInt(applicant.getSocialIntegrationProgramScore()) > 3
                    && Integer.parseInt(applicant.getSejongInstituteScore()) > 3) { // 세종학당 중급2
                if (Float.parseFloat(applicant.getGpa()) > 3.49) { // 우수 장학생
                    workingHours = 35;
                } else {
                    workingHours = 30;
                }
            } else {
                workingHours = 15; // 조건 미달 시 15시간
            }
        }

        return workingHours;
    }

    // 해당 아르바이트의 총 근로시간 조회
    public long calculateTotalWorkHours(Announcement announcement) {
        List<WorkDay> workDays = announcement.getWorkDays();
        long totalMinutes = 0;

        for (WorkDay workDay : workDays) {
            // 요일 가져오기
            DayOfWeek dayOfWeek = workDay.getDay();

            // 월~금에 해당하는 경우만 시간 합 계산
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                LocalTime startTime = workDay.getWorkStartTime();
                LocalTime endTime = workDay.getWorkEndTime();

                // 분으로 변환
                long workMinutes = Duration.between(startTime, endTime).toMinutes();
                totalMinutes += workMinutes;
            }
        }

        // 총 분을 시간으로 변환
        long totalHours = totalMinutes / 60;
        return totalHours;
    }


    // 전체 - 지역으로 분류
    public List<Announcement> filterdRegion(List<String> region, List<Announcement> announcements) {
        // region 값이 1개라도 포함되어있는 경우
        return announcements.stream()
                .filter(announcement ->
                        region.stream().anyMatch(r -> announcement.getOwner().getStoreAddressName().contains(r))
                )
                .toList();
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

    @Transactional
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
                .gender(EGender.getDescriptionBySex(announcement.getGender()))
                .education(EEducation.getDescriptionByType(announcement.getEducation()))
                .addressName(announcement.getOwner().getStoreAddressName())
                .numberRecruited(announcement.getNumberRecruited())
                .content(announcement.getContent())
                .build();

        return announcementDetailDto;
    }
}
