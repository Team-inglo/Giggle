package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.ScheduleCreateDto;
import com.inglo.giggle.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/{partTimeId}")
    @Operation(summary = "스케쥴 편집(생성,수정,삭제)", description = "사용자의 아르바이트 스케쥴을 편집합니다. 스케쥴 생성, 수정, 삭제가 가능합니다.")
    public ResponseDto<?> createSchedule(
            @UserId Long userId,
            @PathVariable Long partTimeId,
            @RequestBody List<ScheduleCreateDto> scheduleCreateDtos
    ) {
        scheduleService.createSchedule(userId, partTimeId, scheduleCreateDtos);
        return ResponseDto.created(null);
    }
    @GetMapping("")
    @Operation(summary = "캘린더 화면에서의 스케쥴 조회", description = "캘린더 화면에서 필요한 해당 년 월의 전체 아르바이트에 대한 스케쥴 및 요약을 조회합니다.")
    public ResponseDto<?> getScheduleForCalendar(
            @UserId Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseDto.ok(scheduleService.getScheduleForCalendar(userId, year, month));
    }

    @GetMapping("/{partTimeId}")
    @Operation(summary = "아르바이트별 스케쥴 조회", description = "아르바이트별 스케쥴을 조회합니다. 스케쥴 편집에서 사용됩니다.")
    public ResponseDto<?> getScheduleOfPartTime(
            @PathVariable Long partTimeId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseDto.ok(scheduleService.getScheduleOfPartTime(partTimeId, year, month));
    }
}
