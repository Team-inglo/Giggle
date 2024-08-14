package com.inglo.giggle.service;

import com.inglo.giggle.domain.Schedule;
import com.inglo.giggle.dto.request.ScheduleCreateDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.PartTimeRepository;
import com.inglo.giggle.repository.ScheduleRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PartTimeRepository partTimeRepository;
    @Transactional
    public void createSchedule(Long userId, ScheduleCreateDto scheduleCreateDto) {
        try {
            scheduleRepository.save(
                    Schedule.builder()
                            .partTime(partTimeRepository.findById(scheduleCreateDto.partTimeId()).orElseThrow(
                                    () -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE)
                            ))
                            .user(userRepository.findById(userId).orElseThrow(
                                    () -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE)
                            ))
                            .startAt(scheduleCreateDto.startAt())
                            .endAt((scheduleCreateDto.endAt()))
                            .build()
            );
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getSchedule(Long userId, Integer year, Integer month) {
        Map<String,Object> result = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository.findAllByUserIdAndYearAndMonth(userId, startOfMonth, endOfMonth);
        result.put("schedules", schedules);

        Map<String, Double> salaryMap = new HashMap<>();

        schedules.forEach(schedule -> {
            String partTimeName = schedule.getPartTime().getPartTimeName();
            double hourlyRate = schedule.getPartTime().getHourlyRate();

            // 근무 시간 계산
            long workedSeconds = java.time.Duration.between(schedule.getStartAt(), schedule.getEndAt()).getSeconds();
            double workedHours = workedSeconds / 3600.0;

            // 해당 알바의 기존 급여 총액을 가져오거나, 없으면 0.0을 기본값으로 설정
            salaryMap.put(partTimeName, salaryMap.getOrDefault(partTimeName, 0.0) + workedHours * hourlyRate);
        });

        result.put("salaries", salaryMap);
        return result;
    }
}
