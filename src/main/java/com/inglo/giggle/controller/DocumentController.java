package com.inglo.giggle.controller;

import com.inglo.giggle.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "지원 서류", description = "사용자의 아르바이트 지원 서류 관련 API")
@RequestMapping("/api/v1")
public class DocumentController {
    private final DocumentService documentService;
}
