package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.response.OwnerAnnouncementStatusDetailDto;
import com.inglo.giggle.dto.response.OwnerAnnouncementStatusListDto;
import com.inglo.giggle.dto.response.UserApplyLogDto;
import com.inglo.giggle.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/announcements/posting")
    @Operation(summary = "아르바이트 공고 등록", description = "사용자가 작성한 서류의 상태 조회")
    public Long createAnnouncement(
            @UserId Long userId,
            @RequestBody AnnouncementCreateDto request
            ) {
        return null; // 공고 id 반환
    }

    @GetMapping("/announcements")
    @Operation(summary = "기업의 공고 관리 리스트 조회", description = "기업이 등록한 공고의 리스트 조회")
    public OwnerAnnouncementStatusListDto getOwnersAnnouncementStatus(
            @UserId Long userId
    ) {
        return null;
    }

    @GetMapping("/announcements/{announcementId}")
    @Operation(summary = "기업의 공고 관리 상세 조회", description = "기업이 등록한 공고의 상세 조회")
    public OwnerAnnouncementStatusDetailDto getOwnersAnnouncementStatusDetails(
            @UserId Long userId,
            @RequestBody Long announcementId
    ) {
        return null;
    }
}
