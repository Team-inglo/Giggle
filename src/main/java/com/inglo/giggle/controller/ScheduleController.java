package com.inglo.giggle.controller;

import com.inglo.giggle.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 스케쥴", description = "사용자의 아르바이트 기록 속, 스케쥴 하나하나 관련된 API")
@RequestMapping("/api/v1")
public class ScheduleController {
    private final ScheduleService scheduleService;
}
