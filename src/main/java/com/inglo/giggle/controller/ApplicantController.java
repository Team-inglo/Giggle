package com.inglo.giggle.controller;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.dto.global.ResponseDto;
import com.inglo.giggle.dto.request.*;
import com.inglo.giggle.service.ApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applicants")
@Tag(name = "지원자(유학생)", description = "지원자(유학생) 관련 API")
public class ApplicantController {
    private final ApplicantService applicantService;

    @PostMapping(value = "/ocr/passport")
    @Operation(summary = "여권 OCR 진행", description = "여권을 등록하여 OCR을 진행하고 데이터를 추출해서 반환합니다.")
    public ResponseDto<?> extractFromPassport(
            @UserId Long userId,
            @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        return ResponseDto.ok(applicantService.extractFromPassport(userId, file));
    }

    @PostMapping(value = "/passport")
    @Operation(summary = "여권 등록", description = "여권을 등록합니다.")
    public ResponseDto<?> updatePassport(@UserId Long userId, @RequestBody PassportUpdateDto passportUpdateDto) {
        applicantService.updatePassport(userId, passportUpdateDto);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/ocr/registration")
    @Operation(summary = "외국인 등록증 OCR 진행", description = "외국인 등록증을 등록하여 OCR을 진행하고 데이터를 추출해서 반환합니다.")
    public ResponseDto<?> extractFromRegistration(
            @UserId Long userId,
            @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        return ResponseDto.ok(applicantService.extractFromRegistration(userId, file));
    }

    @PostMapping(value = "/registration")
    @Operation(summary = "외국인 등록증 등록", description = "외국인 등록증을 등록합니다.")
    public ResponseDto<?> updateRegistration(@UserId Long userId, @RequestBody RegistrationUpdateDto registrationUpdateDto) {
        applicantService.updateRegistration(userId, registrationUpdateDto);
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

    @PostMapping(value = "/ocr/topik")
    @Operation(summary = "스펙 OCR - 토픽", description = "토픽 자격증을 등록하여 데이터를 추출합니다.")
    public ResponseDto<?> extractFromTopik(
            @UserId Long userId,
            @RequestPart(value = "file", required = false)
            MultipartFile file) {
        return ResponseDto.ok(applicantService.extractFromTopik(userId, file));
    }

    @PostMapping(value = "/topik")
    @Operation(summary = "스펙 등록 - 토픽", description = "토픽 점수를 등록합니다.")
    public ResponseDto<?> updateTopik(@UserId Long userId, @RequestBody TopikUpdateDto topikUpdateDto) {
        applicantService.updateTopik(userId, topikUpdateDto);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/ocr/social-integration-program")
    @Operation(summary = "스펙 OCR - 사회통합프로그램", description = "사회통합프로그램 자격증을 등록하여 데이터를 추출합니다.")
    public ResponseDto<?> extractFromSocialIntegrationProgram(
            @UserId Long userId,
            @RequestPart(value = "file", required = false)
            MultipartFile file) {
        return ResponseDto.ok(applicantService.extractFromSocialIntegrationProgram(userId, file));
    }

    @PostMapping(value = "/social-integration-program")
    @Operation(summary = "스펙 등록 - 사회통합프로그램", description = "사회통합프로그램 점수를 등록합니다.")
    public ResponseDto<?> updateSocialIntegrationProgram(@UserId Long userId, @RequestBody SocialIntegrationProgramUpdateDto socialIntegrationProgramUpdateDto) {
        applicantService.updateSocialIntegrationProgram(userId, socialIntegrationProgramUpdateDto);
        return ResponseDto.ok(null);
    }

    @PostMapping(value = "/ocr/sejong-institute")
    @Operation(summary = "스펙 OCR - 세종학당", description = "세종학당 자격증을 등록하여 데이터를 추출합니다.")
    public ResponseDto<?> extractFromSejongInstitute(
            @UserId Long userId,
            @RequestPart(value = "file", required = false)
            MultipartFile file) {
        return ResponseDto.ok(applicantService.extractFromSejongInstitute(userId, file));
    }

    @PostMapping(value = "/sejong-institute")
    @Operation(summary = "스펙 등록 - 세종학당", description = "세종학당 점수를 등록합니다.")
    public ResponseDto<?> updateSejongInstitute(@UserId Long userId, @RequestBody SejongInstituteUpdateDto sejongInstituteUpdateDto) {
        applicantService.updateSejongInstitute(userId, sejongInstituteUpdateDto);
        return ResponseDto.ok(null);
    }
}
