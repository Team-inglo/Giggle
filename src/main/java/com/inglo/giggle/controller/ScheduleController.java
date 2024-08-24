package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.ScheduleCreateDto;
import com.inglo.giggle.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 스케쥴", description = "사용자의 아르바이트 기록 속, 스케쥴 하나하나 관련된 API")
@RequestMapping("/api/v1/applicants/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("")
    public ResponseDto<?> createSchedule(
            @UserId Long userId,
            @RequestBody List<ScheduleCreateDto> scheduleCreateDtos
    ) {
        scheduleService.createSchedule(userId, scheduleCreateDtos);
        return ResponseDto.created(null);
    }
    @GetMapping("")
    public ResponseDto<?> getSchedule(
            @UserId Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseDto.ok(scheduleService.getSchedule(userId, year, month));
    }

    @PatchMapping("/{scheduleId}")
    public ResponseDto<?> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleCreateDto scheduleCreateDto
    ) {
        scheduleService.updateSchedule(scheduleId, scheduleCreateDto);
        return ResponseDto.ok(null);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseDto<?> deleteSchedule(
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseDto.ok(null);
    }
}
