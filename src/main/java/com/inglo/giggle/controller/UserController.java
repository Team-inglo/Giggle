package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "본인 정보 조회", description = "본인 정보를 조회합니다.")
    public ResponseDto<?> readUsers(
            @UserId Long userId
    ) {
        return ResponseDto.ok(userService.readUser(userId));
    }

    @PatchMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "본인 정보 수정(스펙 등록)", description = "자격증을 등록하여 본인 정보를 업데이트합니다..")
    public ResponseDto<?> updateUser(
            @UserId Long userId,
            @RequestParam(required = false) String nickname,
            @RequestPart(value = "file", required = false)
            MultipartFile imgFile) {
        // ToDo: OCR 사용하여 자격증 파일 읽어와, 자격증 정보를 등록하고, 유저 정보 업데이트
        userService.updateUser(userId);
        return ResponseDto.ok(null);
    }

}
