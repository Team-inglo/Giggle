package com.inglo.giggle.domain;

import com.inglo.giggle.annotation.Date;
import com.inglo.giggle.dto.request.*;
import com.inglo.giggle.dto.response.PassportDto;
import com.inglo.giggle.dto.response.RegistrationDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "applicant")
public class Applicant {
    @Id
    @Column(name = "applicant_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "applicant_id")
    private User user;

    @Column(name = "address_name")
    private String addressName = "None";

    @Column(name = "address_x")
    private Float addressX = 0f;

    @Column(name = "address_y")
    private Float addressY = 0f;

    @Column(name = "gpa")
    private String gpa="0";

    @Column(name = "startDay")
    private LocalDateTime startDay= null;

    @Column(name = "endDay")
    private LocalDateTime endDay= null;

    /* User Info - passport */
    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "passport_issue_date")
    private String passportIssueDate;

    @Column(name = "passport_expiry_date")
    private String passportExpiryDate;

    /* User Info - registration */
    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "status_of_residence")
    private String statusOfResidence;

    @Column(name = "registration_issue_date")
    private String registrationIssueDate;

    /* User Info - User Spec */
    @Column(name = "topik_score")
    private String topikScore = "0";

    @Column(name = "social_integration_program_score")
    private String socialIntegrationProgramScore = "0";

    @Column(name = "sejong_institute_score")
    private String sejongInstituteScore = "0";

    @Builder
    public Applicant(User user) {
        this.user = user;
    }

    public static Applicant signUp(User user) {
        return Applicant.builder()
                .user(user)
                .build();
    }
    public void updateApplicant(UpdateApplicantDto updateApplicantDto) {
        if (updateApplicantDto.addressName() != null && (!this.addressName.equals(updateApplicantDto.addressName())))
            this.addressName = updateApplicantDto.addressName();
        if (updateApplicantDto.addressX() != null && (!this.addressX.equals(updateApplicantDto.addressX())))
            this.addressX = updateApplicantDto.addressX();
        if (updateApplicantDto.addressY() != null && (!this.addressY.equals(updateApplicantDto.addressY())))
            this.addressY = updateApplicantDto.addressY();
        if (updateApplicantDto.gpa() != null && (!this.gpa.equals(updateApplicantDto.gpa())))
            this.gpa = updateApplicantDto.gpa();
        if (updateApplicantDto.startDay() != null && (!this.startDay.equals(updateApplicantDto.startDay())))
            this.startDay = updateApplicantDto.startDay();
        if (updateApplicantDto.endDay() != null && (!this.endDay.equals(updateApplicantDto.endDay())))
            this.endDay = updateApplicantDto.endDay();
    }

    public void registerPassport(PassportUpdateDto passportDto) {
        this.passportNumber = passportDto.passportNumber();
        this.name = passportDto.name();
        this.sex = passportDto.sex();
        this.dateOfBirth = passportDto.dateOfBirth();
        this.nationality = passportDto.nationality();
        this.passportIssueDate = passportDto.passportIssueDate();
        this.passportExpiryDate = passportDto.passportExpiryDate();
    }

    public void registerRegistration(RegistrationUpdateDto registrationDto) {
        this.registrationNumber = registrationDto.registrationNumber();
        this.statusOfResidence = registrationDto.statusOfResidence();
        this.registrationIssueDate = registrationDto.registrationIssueDate();
    }

    public void registerTopik(TopikUpdateDto topikUpdateDto){
        this.topikScore = topikUpdateDto.topikScore();
    }
    public void registerSocialIntegrationProgram(SocialIntegrationProgramUpdateDto socialIntegrationProgramUpdateDto){
        this.socialIntegrationProgramScore = socialIntegrationProgramUpdateDto.socialIntegrationProgramScore();
    }
    public void registerSejongInstitute(SejongInstituteUpdateDto sejongInstituteUpdateDto){
        this.sejongInstituteScore = sejongInstituteUpdateDto.sejongInstituteScore();
    }
}
