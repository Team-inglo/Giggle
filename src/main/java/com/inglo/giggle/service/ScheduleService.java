package com.inglo.giggle.service;

import com.inglo.giggle.domain.Schedule;
import com.inglo.giggle.dto.request.ScheduleCreateDto;
import com.inglo.giggle.dto.response.ScheduleDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.PartTimeRepository;
import com.inglo.giggle.repository.ScheduleRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PartTimeRepository partTimeRepository;
    @Transactional
    public void createSchedule(Long userId, List<ScheduleCreateDto> scheduleCreateDtos) {
        try {
            scheduleCreateDtos.forEach(scheduleCreateDto -> {
                if(scheduleCreateDto.startAt().isAfter(scheduleCreateDto.endAt()) || scheduleCreateDto.startAt().isEqual(scheduleCreateDto.endAt())) {
                    throw new CommonException(ErrorCode.START_AT_AFTER_END_AT);
                }
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
                    }
            );
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    public Map<String, Object> getScheduleForCalendar(Long userId, Integer year, Integer month) {
        AtomicReference<Double> totalSalary = new AtomicReference<>(0.0);
        Map<String,Object> result = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<ScheduleDto> scheduleDtos = scheduleRepository.findAllByUserIdAndYearAndMonth(userId, startOfMonth, endOfMonth).stream()
                        .map(schedule -> ScheduleDto.builder()
                                .id(schedule.getId())
                                .partTimeName(schedule.getPartTime().getPartTimeName())
                                .hourlyRate(schedule.getPartTime().getHourlyRate())
                                .startAt(schedule.getStartAt())
                                .endAt(schedule.getEndAt())
                                .build()
                        ).toList();
        result.put("schedules", scheduleDtos);
        List<Map<String,Object>> summaryList = new ArrayList<>();
        Map<String, Double> salaryMap = new HashMap<>();
        Map<String, Double> totalHourMap = new HashMap<>();

        scheduleDtos.forEach(schedule -> {
            String partTimeName = schedule.partTimeName();
            double hourlyRate = schedule.hourlyRate();

            // 근무 시간 계산
            long workedSeconds = java.time.Duration.between(schedule.startAt(), schedule.endAt()).getSeconds();
            double workedHours = workedSeconds / 3600.0;
            totalSalary.updateAndGet(v -> v + workedHours * hourlyRate);

            // 해당 알바의 기존 급여 총액을 가져오거나, 없으면 0.0을 기본값으로 설정
            salaryMap.put(partTimeName, salaryMap.getOrDefault(partTimeName, 0.0) + workedHours * hourlyRate);
            totalHourMap.put(partTimeName, totalHourMap.getOrDefault(partTimeName, 0.0) + workedHours);
        });
        salaryMap.forEach((partTimeName, salary) -> {
                    Map<String, Object> summaryMap = new HashMap<>();
                    summaryMap.put("name", partTimeName);
                    summaryMap.put("salary", salary);
                    summaryMap.put("totalHour", totalHourMap.get(partTimeName));
                    summaryList.add(summaryMap);
                });
        result.put("totalSalary", totalSalary.get());
        result.put("summary", summaryList);

        return result;
    }

    public List<ScheduleDto> getScheduleOfPartTime(Long partTimeId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return scheduleRepository.findAllByPartTimeIdAndYearAndMonth(partTimeId, startOfMonth, endOfMonth).stream()
                .map(schedule -> ScheduleDto.builder()
                        .id(schedule.getId())
                        .partTimeName(schedule.getPartTime().getPartTimeName())
                        .hourlyRate(schedule.getPartTime().getHourlyRate())
                        .startAt(schedule.getStartAt())
                        .endAt(schedule.getEndAt())
                        .build()
                ).toList();
    }

    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleCreateDto scheduleCreateDto) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        if ((scheduleCreateDto.startAt()!=null && scheduleCreateDto.endAt()!=null) && (scheduleCreateDto.startAt().isAfter(scheduleCreateDto.endAt()) || scheduleCreateDto.startAt().isEqual(scheduleCreateDto.endAt()))) {
            throw new CommonException(ErrorCode.START_AT_AFTER_END_AT);
        }
        else if ((scheduleCreateDto.startAt() == null && scheduleCreateDto.endAt()!=null) && (scheduleCreateDto.endAt().isBefore(schedule.getStartAt()) || scheduleCreateDto.endAt().isEqual(schedule.getStartAt()))) {
            throw new CommonException(ErrorCode.START_AT_AFTER_END_AT);
        }
        else if ((scheduleCreateDto.startAt() != null && scheduleCreateDto.endAt() == null) && (scheduleCreateDto.startAt().isAfter(schedule.getEndAt()) || scheduleCreateDto.startAt().isEqual(schedule.getEndAt()))) {
            throw new CommonException(ErrorCode.START_AT_AFTER_END_AT);
        }
        else {
            try {
                schedule.updateSchedule(
                        scheduleCreateDto.startAt(),
                        scheduleCreateDto.endAt()
                );
            } catch (Exception e) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        try {
            scheduleRepository.delete(schedule);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
