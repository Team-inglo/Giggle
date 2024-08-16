package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.response.DocumentLogDto;
import com.inglo.giggle.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "서명요청", description = "서명요청 API")
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("/request")
    @Operation(summary = "서명 요청", description = "서명 요청")
    public String requestSignature(
            @RequestBody List<RequestSignatureDto> request,
            @RequestParam Integer docs_number,
            @UserId Long userId
    ) {
        String embeddedUrl = documentService.requestSinature(request, docs_number, userId);

        return embeddedUrl;
    }
}
