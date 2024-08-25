package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.UpdateOwnerDto;
import com.inglo.giggle.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final OwnerService ownerService;

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
