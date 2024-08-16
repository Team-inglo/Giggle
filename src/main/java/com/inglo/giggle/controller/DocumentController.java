package com.inglo.giggle.controller;

import com.inglo.giggle.dto.request.ParticipantMappingRequestDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.response.DocumentLogDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.service.DocumentService;
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
    @Tag(name = "서명요청", description = "서명요청 API")
    public String requestPartTimeEmploymentPermit(
            @RequestBody List<ParticipantMappingRequestDto> request,
            @RequestParam Integer docs_number
    ) {
        return documentService.submitDocument(request, docs_number);
    }

    @GetMapping("/status")
    @Tag(name = "요청조회", description = "요청조회 API")
    public DocumentLogDto getDocumentsStatus(
    ) {
        return null;
    }
}
