package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.PartTimeCreateDto;
import com.inglo.giggle.service.PartTimeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "아르바이트 기록", description = "사용자의 아르바이트 기록 관련 API")
@RequestMapping("/api/v1/applicants/part-times")
public class PartTimeController {
    private final PartTimeService partTimeService;

    @PostMapping("")
    public ResponseDto<?> createPartTime(
            @UserId Long userId,
            @RequestBody PartTimeCreateDto partTimeCreateDto
    ) {
        partTimeService.createPartTime(userId, partTimeCreateDto);
        return ResponseDto.created(null);
    }

    @GetMapping("")
    public ResponseDto<?> getPartTimes(
            @UserId Long userId
    ) {
        return ResponseDto.ok(partTimeService.getPartTimes(userId));
    }

    @GetMapping("/{partTimeId}")
    public ResponseDto<?> getPartTime(
            @PathVariable Long partTimeId
    ) {
        return ResponseDto.ok(partTimeService.getPartTime(partTimeId));
    }

    @PatchMapping("/{partTimeId}")
    public ResponseDto<?> updatePartTime(
            @PathVariable Long partTimeId,
            @RequestBody PartTimeCreateDto partTimeCreateDto
    ) {
        partTimeService.updatePartTime(partTimeId, partTimeCreateDto);
        return ResponseDto.ok(null);
    }

    @DeleteMapping("/{partTimeId}")
    public ResponseDto<?> deletePartTime(
            @PathVariable Long partTimeId
    ) {
        partTimeService.deletePartTime(partTimeId);
        return ResponseDto.ok(null);
    }
}
