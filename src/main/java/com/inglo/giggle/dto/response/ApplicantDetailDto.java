package com.inglo.giggle.dto.response;

import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Builder
@Schema(name = "ApplicationDetailDto", description = "지원자(유학생) 상세 정보 조회 Dto")
public record ApplicantDetailDto (
        Long userId,
        String addressName,
        Float addressX,
        Float addressY,
        String passportNumber,
        String name,
        String sex,
        String dateOfBirth,
        String nationality,
        String passportIssueDate,
        String passportExpiryDate,
        String registrationNumber,
        String statusOfResidence,
        String registrationIssueDate,
        String topikScore,
        String socialIntegrationProgramScore,
        String sejongInstituteScore,
        String gpa,
        String periodOfStay
) implements UserDetailDto {
        public static ApplicantDetailDto fromEntity(User user, Applicant applicant) {
            return ApplicantDetailDto.builder()
                    .userId(user.getId())
                    .addressName(applicant.getAddressName())
                    .addressX(applicant.getAddressX())
                    .addressY(applicant.getAddressY())
                    .passportNumber(applicant.getPassportNumber())
                    .name(applicant.getName())
                    .sex(applicant.getSex())
                    .dateOfBirth(applicant.getDateOfBirth())
                    .nationality(applicant.getNationality())
                    .passportIssueDate(applicant.getPassportIssueDate())
                    .passportExpiryDate(applicant.getPassportExpiryDate())
                    .registrationNumber(applicant.getRegistrationNumber())
                    .statusOfResidence(applicant.getStatusOfResidence())
                    .registrationIssueDate(applicant.getRegistrationIssueDate())
                    .topikScore(applicant.getTopikScore())
                    .socialIntegrationProgramScore(applicant.getSocialIntegrationProgramScore())
                    .sejongInstituteScore(applicant.getSejongInstituteScore())
                    .gpa(applicant.getGpa())
                    .periodOfStay("D-" + ChronoUnit.DAYS.between(LocalDateTime.now(),applicant.getEndDay()))
                    .build();
        }
}
