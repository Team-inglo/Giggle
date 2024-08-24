package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.response.UserApplyDetailDto;
import com.inglo.giggle.dto.response.UserApplyLogDto;
import com.inglo.giggle.service.ApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 지원", description = "사용자의 아르바이트 지원 관련 API")
@RequestMapping("/api/v1/applys")
public class ApplyController {
    private final ApplyService applyService;

    @GetMapping("/logs")
    @Operation(summary = "서류 상태 조회", description = "사용자가 작성한 서류의 상태 조회")
    public UserApplyLogDto getUserApplyLogs(
            @UserId Long userId,
            @RequestParam Boolean status
    ) {
        UserApplyLogDto userApplyLogDto = applyService.getUserApplyLogs(userId, status);
        return userApplyLogDto;
    }

    @GetMapping("/details/{applyId}")
    @Operation(summary = "서류 상세 조회", description = "사용자가 작성한 서류의 상세 조회")
    public UserApplyDetailDto getUserApplyDetails(
            @UserId Long userId,
            @PathVariable Long applyId

    ) {
        UserApplyDetailDto userApplyDetails = applyService.getUserApplyDetails(userId, applyId);
        return userApplyDetails;
    }
}
