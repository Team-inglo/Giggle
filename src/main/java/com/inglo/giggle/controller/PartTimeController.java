package com.inglo.giggle.controller;

import com.inglo.giggle.service.PartTimeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 기록", description = "사용자의 아르바이트 기록 관련 API")
@RequestMapping("/api/v1")
public class PartTimeController {
    private final PartTimeService partTimeService;
}
