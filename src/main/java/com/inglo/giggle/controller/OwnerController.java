package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.request.UpdateOwnerDto;
import com.inglo.giggle.dto.response.OwnerAnnouncementStatusDetailDto;
import com.inglo.giggle.dto.response.OwnerAnnouncementStatusListDto;
import com.inglo.giggle.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "점주", description = "점주 관련 API")
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/announcements/posting")
    @Operation(summary = "아르바이트 공고 등록", description = "사용자가 작성한 서류의 상태 조회")
    public Long createAnnouncement(
            @UserId Long userId,
            @RequestBody AnnouncementCreateDto request
            ) {
        Long announcementId = ownerService.createAnnounement(request, userId);
        return announcementId; // 공고 id 반환
    }

    @GetMapping("/announcements")
    @Operation(summary = "기업의 공고 관리 리스트 조회", description = "기업이 등록한 공고의 리스트 조회")
    public OwnerAnnouncementStatusListDto getOwnersAnnouncementStatus(
            @UserId Long userId
    ) {
        OwnerAnnouncementStatusListDto ownerAnnouncementStatusListDto = ownerService.getOwnerAnnouncementStatusList(userId);
        return ownerAnnouncementStatusListDto;
    }

    @GetMapping("/announcements/{announcementId}")
    @Operation(summary = "기업의 공고 관리 상세 조회", description = "기업이 등록한 공고의 상세 조회")
    public OwnerAnnouncementStatusDetailDto getOwnersAnnouncementStatusDetails(
            @UserId Long userId,
            @PathVariable Long announcementId
    ) {
        OwnerAnnouncementStatusDetailDto ownerAnnouncementStatusDetailDto = ownerService.getOwnerAnnouncementStatusDetails(userId, announcementId);
        return ownerAnnouncementStatusDetailDto;
    }

    @PatchMapping("")
    @Operation(summary = "owner 상세정보 수정", description = "owner 상세정보를 수정합니다.")
    public ResponseDto<?> updateOwner(
            @UserId Long userId,
            @RequestBody UpdateOwnerDto updateOwnerDto
    ) {
        ownerService.updateOwner(userId, updateOwnerDto);
        return ResponseDto.ok(null);
    }
}
