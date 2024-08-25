package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.response.AnnouncementDetailDto;
import com.inglo.giggle.dto.response.AnnouncementListDto;
import com.inglo.giggle.dto.response.UserApplyLogDto;
import com.inglo.giggle.dto.type.EAnnouncementPeriod;
import com.inglo.giggle.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.RequestPath;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 공고", description = "아르바이트 공고 관련 API")
@RequestMapping("/api/v1/users/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @GetMapping("")
    @Operation(summary = "아르바이트 공고 리스트 카테고리로 조회", description = "아르바이트 공고 리스트 카테고리로 조회")
    public AnnouncementListDto getAnnounceMentsForCategory(
            @UserId Long userId,
            @RequestParam(required = false) String sortBy,            // 정렬 기준: ALL, RECOMMENDATION
            @RequestParam(required = false) Boolean isOwner,          // 관심 여부: TRUE(자신이 작성한 공고만), FALSE(전체 공고)
            @RequestParam(required = false) String jobType, // 업직종
            @RequestParam(required = false) String sortOrder,         // 정렬 순서: ASC(오름차순), DESC(내림차순)
            @RequestParam(required = false) List<String> region,            // 근무 지역: 지역명, 여러 지역은 쉼표(,)로 구분
            @RequestParam(required = false) EAnnouncementPeriod period             // 기간: ALL(전체), OPEN(신청중), UPCOMING(신청예정), CLOSED(신청마감)
    ) {
        return null;
    }

    @GetMapping("/{announcementId}")
    @Operation(summary = "특정 아르바이트 공고 상세 조회", description = "특정 아르바이트 공고 상세 조회")
    public AnnouncementDetailDto getAnnounceMentDetails(
            @UserId Long userId,
            @PathVariable Long announcementId
    ) {
        return null;
    }
}
