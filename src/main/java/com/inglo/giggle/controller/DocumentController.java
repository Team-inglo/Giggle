package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.request.WebHookRequestDto;
import com.inglo.giggle.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "지원 서류", description = "사용자의 아르바이트 지원 서류 관련 API")
@RequestMapping("/api/v1")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("/applicants/documents/request")
    @Operation(summary = "서명 요청", description = "서명 요청")
    public String requestSignature(
            @RequestBody List<RequestSignatureDto> request,
            @RequestParam String documentType,
            @RequestParam Long announcementId,
            @UserId Long userId
    ) {
        String embeddedUrl = documentService.requestSignature(request, documentType, announcementId, userId);

        return embeddedUrl;
    }

    @PostMapping("/webhook")
    @Operation(summary = "웹훅용 api", description = "웹훅용 api")
    public void requestSignature(
            @RequestBody WebHookRequestDto request
            ) {
        documentService.requestWebHook(request);
    }
}
