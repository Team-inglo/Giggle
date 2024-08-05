package com.inglo.giggle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.type.EProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(name = "UserDetailDto", description = "유저 상세 정보 조회 Dto")
public record UserDetailDto(
        Long id,
        String address,
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
        String sejongInstituteScore
) {
        public static UserDetailDto fromEntity(User user) {
            return UserDetailDto.builder()
                    .id(user.getId())
                    .address(user.getAddress())
                    .passportNumber(user.getPassportNumber())
                    .name(user.getName())
                    .sex(user.getSex())
                    .dateOfBirth(user.getDateOfBirth())
                    .nationality(user.getNationality())
                    .passportIssueDate(user.getPassportIssueDate())
                    .passportExpiryDate(user.getPassportExpiryDate())
                    .registrationNumber(user.getRegistrationNumber())
                    .statusOfResidence(user.getStatusOfResidence())
                    .registrationIssueDate(user.getRegistrationIssueDate())
                    .topikScore(user.getTopikScore())
                    .socialIntegrationProgramScore(user.getSocialIntegrationProgramScore())
                    .sejongInstituteScore(user.getSejongInstituteScore())
                    .build();
        }
}
