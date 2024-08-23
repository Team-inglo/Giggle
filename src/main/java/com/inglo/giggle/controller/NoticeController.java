package com.inglo.giggle.controller;

import com.inglo.giggle.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "알림", description = "알림 관련 API")
@RequestMapping("/api/v1/users")
public class NoticeController {
    private final NoticeService noticeService;
}
