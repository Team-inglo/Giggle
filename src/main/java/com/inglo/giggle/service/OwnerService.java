package com.inglo.giggle.service;

import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.domain.WorkDay;
import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.response.AnnouncementListDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.AnnouncementRepository;
import com.inglo.giggle.repository.OwnerRepository;
import com.inglo.giggle.repository.UserRepository;
import com.inglo.giggle.repository.WorkDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;

    // 아르바이트 공고 생성 post method
    public Long createAnnounement(AnnouncementCreateDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));

        Announcement announcement = Announcement.builder()
                .owner(owner)
                .title(request.title())
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
}
