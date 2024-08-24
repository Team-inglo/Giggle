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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PartTimeRepository partTimeRepository;

    @Transactional
    public void createSchedule(Long userId, Long partTimeId, List<ScheduleCreateDto> requestSchedules) {
        try {
            // 전달받은 스케쥴에서 날짜 리스트 추출
            List<LocalDate> requestDates = requestSchedules.stream()
                    .map(schedule -> schedule.startAt().toLocalDate())
                    .distinct()
                    .toList();

            // DB에서 유저의 모든 스케줄의 날짜 가져오기
            List<Schedule> existingSchedules = scheduleRepository.findAllByPartTimeId(partTimeId);

            for (ScheduleCreateDto requestSchedule : requestSchedules) {  // 스케줄 생성 및 수정 처리
                // 스케쥴에 대한 유효성 검사
                if(requestSchedule.startAt().isAfter(requestSchedule.endAt()) || requestSchedule.startAt().isEqual(requestSchedule.endAt())) {
                    throw new CommonException(ErrorCode.START_AT_AFTER_END_AT);
                }
                LocalDate requestDate = requestSchedule.startAt().toLocalDate();

                // DB에 해당 날짜가 존재하는지 확인
                Optional<Schedule> matchingSchedule = existingSchedules.stream()
                        .filter(schedule -> schedule.getStartAt().toLocalDate().equals(requestDate))
                        .findFirst();

                if (matchingSchedule.isPresent()) {
                    // 겹치는 스케줄이 있으면 수정
                    Schedule scheduleToUpdate = matchingSchedule.get();
                    scheduleToUpdate.updateSchedule(requestSchedule.startAt(), requestSchedule.endAt());
                } else {
                    // 겹치는 스케줄이 없으면 새로 생성
                    Schedule newSchedule = Schedule.builder()
                            .partTime(partTimeRepository.findById(partTimeId)
                                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE)))
                            .user(userRepository.findById(userId)
                                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE)))
                            .startAt(requestSchedule.startAt())
                            .endAt(requestSchedule.endAt())
                            .build();
                    scheduleRepository.save(newSchedule);
                }
            }
            // DB에만 존재하고, 요청된 스케줄에는 없는 날짜에 해당하는 스케줄 삭제
            for (Schedule existingSchedule : existingSchedules) {
                LocalDate existingDate = existingSchedule.getStartAt().toLocalDate();
                if (!requestDates.contains(existingDate)) {
                    scheduleRepository.deleteByUserIdAndStartAt(userId, existingSchedule.getStartAt());
                }
            }

        } catch (Exception e) {
            log.error("Failed to create schedule", e);
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
}
