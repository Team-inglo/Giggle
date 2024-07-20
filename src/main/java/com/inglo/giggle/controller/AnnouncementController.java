package com.inglo.giggle.controller;

import com.inglo.giggle.service.AnnouncementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 공고", description = "아르바이트 공고 관련 API")
@RequestMapping("/api/v1")
public class AnnouncementController {
    private final AnnouncementService announcementService;
}
