package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.response.UserApplyLogDto;
import com.inglo.giggle.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.RequestPath;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 공고", description = "아르바이트 공고 관련 API")
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @GetMapping("")
    @Operation(summary = "아르바이트 공고 리스트 카테고리로 조회", description = "아르바이트 공고 리스트 카테고리로 조회")
    public UserApplyLogDto getAnnounceMentsForCategory(
            @RequestParam
            @RequestParam
            @RequestParam
            @RequestParam
    ) {
        return null;
    }

    @GetMapping("/{announcementId}")
    @Operation(summary = "특정 아르바이트 공고 상세 조회", description = "특정 아르바이트 공고 상세 조회")
    public UserApplyLogDto getUserApplyLogs(
            @PathVariable Long announcementId
    ) {
        return null;
    }

    @GetMapping("/postings")
    @Operation(summary = "아르바이트 공고 등록", description = "사용자가 작성한 서류의 상태 조회")
    public UserApplyLogDto getUserApplyLogs(
            @RequestBody
    ) {
        UserApplyLogDto userApplyLogDto = applyService.getUserApplyLogs(userId, status);
        return userApplyLogDto;
    }
}
