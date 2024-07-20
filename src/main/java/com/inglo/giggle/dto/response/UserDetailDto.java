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
        String name,
        String address,
        String passportNumber,
        String statusOfResidence,
        String periodOfStay,
        String passportIssueDate,
        String nationality,
        String registrationIssueDate,
        String registrationNumber,
        String topikScore,
        String socialIntegrationProgramScore,
        String sejongInstituteScore

) {
        public static UserDetailDto fromEntity(User user) {
            return UserDetailDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .address(user.getAddress())
                    .passportNumber(user.getPassportNumber())
                    .statusOfResidence(user.getStatusOfResidence())
                    .periodOfStay(user.getPeriodOfStay())
                    .passportIssueDate(user.getPassportIssueDate())
                    .nationality(user.getNationality())
                    .registrationIssueDate(user.getRegistrationIssueDate())
                    .registrationNumber(user.getRegistrationNumber())
                    .topikScore(user.getTopikScore())
                    .socialIntegrationProgramScore(user.getSocialIntegrationProgramScore())
                    .sejongInstituteScore(user.getSejongInstituteScore())
                    .build();
        }
}
