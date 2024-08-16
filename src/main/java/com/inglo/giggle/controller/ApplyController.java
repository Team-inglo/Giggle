package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.response.UserApplyLogDto;
import com.inglo.giggle.service.ApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 지원", description = "사용자의 아르바이트 지원 관련 API")
@RequestMapping("/api/v1")
public class ApplyController {
    private final ApplyService applyService;

    @GetMapping("/status")
    @Operation(summary = "서류 상태 조회", description = "사용자가 작성한 서류의 상태 조회")
    public UserApplyLogDto getUserApplyLogs(
            @UserId Long userId
    ) {
        UserApplyLogDto userApplyLogDto = applyService.getUserApplyLogs(userId);
        return userApplyLogDto;
    }
}
