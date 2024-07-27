package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public record TemplateRequestDTO (
) {
    // 시간제 취업허가 서류 request dto
    public record PartTimeWorkEmployee(
            @JsonProperty("major") @Schema(description = "전공", example = "컴퓨터공학전공")
            @NotNull(message = "전공은 null이 될 수 없습니다.")
            String major,
            @JsonProperty("term_of_completion") @Schema(description = "이수학기", example = "6")
            @NotNull(message = "이수학기는 null이 될 수 없습니다.")
            int TermOfCompletion,
            @JsonProperty("email") @Schema(description = "이메일", example = "test88@gmail.com")
            @NotNull(message = "이메일은 null이 될 수 없습니다.")
            String email
    ) {}

    public record PartTimeWorkEmployer(
            @JsonProperty("company_name") @Schema(description = "업체명", example = "만리지화")
            @NotNull(message = "업체명은 null이 될 수 없습니다.")
            String companyName,
            @JsonProperty("business_number") @Schema(description = "사업자등록번호", example = "123-456-789-10")
            @NotNull(message = "사업자등록번호는 null이 될 수 없습니다.")
            String businessNumber,
            @JsonProperty("address") @Schema(description = "주소", example = "서울시 중구 동국대학교")
            @NotNull(message = "주소는 null이 될 수 없습니다.")
            String address,
            @JsonProperty("category") @Schema(description = "업종", example = "서빙")
            @NotNull(message = "업종은 null이 될 수 없습니다.")
            String category,
            @JsonProperty("phone_number") @Schema(description = "전화번호", example = "02-7780-8830")
            @NotNull(message = "전화번호는 null이 될 수 없습니다.")
            String phoneNumber,
            @JsonProperty("period") @Schema(description = "근무기간", example = "2-12")
            @NotNull(message = "기간은 null이 될 수 없습니다.")
            LocalDate period,
            @JsonProperty("name") @Schema(description = "고용주이름", example = "고영준")
            @NotNull(message = "고용주의 이름은 null이 될 수 없습니다.")
            String name,
            @JsonProperty("salary") @Schema(description = "급여", example = "9760")
            @NotNull(message = "급여는 null이 될 수 없습니다.")
            int salary,
            @JsonProperty("weekday_working_hour") @Schema(description = "평일근무시간", example = "8:30")
            @NotNull(message = "평일 근무시간은 null이 될 수 없습니다.")
            LocalTime weekdayWorkingHour,
            @JsonProperty("weekend_working_hour") @Schema(description = "주말근무시간", example = "10:30")
            @NotNull(message = "주말 근무시간은 null이 될 수 없습니다.")
            LocalTime weekendWorkingHour
            ) {}

    public record PartTimeWorkRepresentation(
            @JsonProperty("date") @Schema(description = "최종결제일자", example = "2024-07-27")
            @NotNull(message = "날짜는 null이 될 수 없습니다.")
            LocalDate date,
            @JsonProperty("name_of_university") @Schema(description = "대학이름", example = "동국")
            @NotNull(message = "대학은 null이 될 수 없습니다.")
            String NameOfUniversity,
            @JsonProperty("name") @Schema(description = "담당자이름", example = "유담영")
            @NotNull(message = "이름은 null이 될 수 없습니다.")
            String name,
            @JsonProperty("phone_number") @Schema(description = "전화번호", example = "123-4567-890")
            @NotNull(message = "번호는 null이 될 수 없습니다.")
            String phoneNumber
    ) {}
}