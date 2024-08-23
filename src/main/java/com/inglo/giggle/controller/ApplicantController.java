package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.UpdateApplicantDto;
import com.inglo.giggle.service.ApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applicants")
@Tag(name = "지원자(유학생)", description = "지원자(유학생) 관련 API")
public class ApplicantController {
    private final ApplicantService applicantService;

    @PostMapping(value = "/passport", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "여권 등록", description = "여권 정보를 등록합니다.")
    public ResponseDto<?> updatePassport(
            @UserId Long userId,
            @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        applicantService.updatePassport(userId, file);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/registration", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "외국인 등록증 등록", description = "외국인 등록증을 등록합니다.")
    public ResponseDto<?> updateRegistration(
            @UserId Long userId,
            @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        applicantService.updateRegistration(userId, file);
        return ResponseDto.ok(null);
    }

    @PatchMapping("")
    @Operation(summary = "applicant 정보 수정", description = "applicant 정보 중 수정 가능한 gpa, startDay, endDay, address를 수정합니다.")
    public ResponseDto<?> updateAddress(
            @UserId Long userId,
            @RequestBody UpdateApplicantDto updateApplicantDto
    ) {
        applicantService.updateApplicant(userId, updateApplicantDto);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/topik", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "스펙 등록(토픽)", description = "토픽 자격증을 등록하여 본인 정보를 업데이트합니다.")
    public ResponseDto<?> updateTopik(
            @UserId Long userId,
            @RequestPart(value = "file", required = false)
            MultipartFile file) {
        // ToDo: OCR 사용하여 자격증 파일 읽어와, 자격증 정보를 등록하고, 유저 정보 업데이트
        applicantService.updateTopik(userId, file);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/social-integration-program", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "스펙 등록(사회통합프로그램)", description = "사회통합프로그램 자격증을 등록하여 본인 정보를 업데이트합니다.")
    public ResponseDto<?> updateSocialIntegrationProgram(
            @UserId Long userId,
            @RequestPart(value = "file", required = false)
            MultipartFile file) {
        applicantService.updateSocialIntegrationProgram(userId, file);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/sejong-institute", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "스펙 등록(세종학당)", description = "세종학당 자격증을 등록하여 본인 정보를 업데이트합니다.")
    public ResponseDto<?> updateSejongInstitute(
            @UserId Long userId,
            @RequestPart(value = "file", required = false)
            MultipartFile file) {
        applicantService.updateSejongInstitute(userId, file);
        return ResponseDto.ok(null);
    }
}
