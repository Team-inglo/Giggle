package com.inglo.giggle.domain;

import com.inglo.giggle.annotation.Date;
import com.inglo.giggle.dto.request.UpdateApplicantDto;
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

    @Column(name = "address")
    private String address="";

    @Column(name = "gpa")
    private String gpa="0";

    @Column(name = "startDay")
    private LocalDateTime startDay= null;

    @Column(name = "endDay")
    private LocalDateTime endDay= null;

    /* User Info - passport */
    @Column(name = "passport_number")
    private String passportNumber="None";

    @Column(name = "name")
    private String name="None";

    @Column(name = "sex")
    private String sex="None";

    @Column(name = "date_of_birth")
    private String dateOfBirth="None";

    @Column(name = "nationality")
    private String nationality="None";

    @Column(name = "passport_issue_date")
    private String passportIssueDate="None";

    @Column(name = "passport_expiry_date")
    private String passportExpiryDate="None";

    /* User Info - registration */
    @Column(name = "registration_number")
    private String registrationNumber="None";

    @Column(name = "status_of_residence")
    private String statusOfResidence="None";

    @Column(name = "registration_issue_date")
    private String registrationIssueDate="None";

    /* User Info - User Spec */
    @Column(name = "topik_score")
    private String topikScore = "0";

    @Column(name = "social_integration_program_score")
    private String socialIntegrationProgramScore = "0";

    @Column(name = "sejong_institute_score")
    private String sejongInstituteScore = "0";

    @Builder
    public Applicant(User user, String address, String gpa, LocalDateTime startDay, LocalDateTime endDay) {
        this.user = user;
        this.address = address;
        this.gpa = gpa;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public static Applicant signUp(User user) {
        return Applicant.builder()
                .user(user)
                .address("")
                .gpa("0")
                .startDay(LocalDateTime.now())
                .endDay(LocalDateTime.now())
                .build();
    }
    public void updateApplicant(UpdateApplicantDto updateApplicantDto) {
        if (updateApplicantDto.address() != null && (!this.address.equals(updateApplicantDto.address())))
            this.address = updateApplicantDto.address();
        if (updateApplicantDto.gpa() != null && (!this.gpa.equals(updateApplicantDto.gpa())))
            this.gpa = updateApplicantDto.gpa();
        if (updateApplicantDto.startDay() != null && (!this.startDay.equals(updateApplicantDto.startDay())))
            this.startDay = updateApplicantDto.startDay();
        if (updateApplicantDto.endDay() != null && (!this.endDay.equals(updateApplicantDto.endDay())))
            this.endDay = updateApplicantDto.endDay();
    }

    public void registerPassport(String passportNumber, String name, String sex, String dateOfBirth, String nationality, String passportIssueDate, String passportExpiryDate) {
        this.passportNumber = passportNumber;
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.passportIssueDate = passportIssueDate;
        this.passportExpiryDate = passportExpiryDate;
    }

    public void registerRegistration(String registrationNumber, String statusOfResidence, String registrationIssueDate) {
        this.registrationNumber = registrationNumber;
        this.statusOfResidence = statusOfResidence;
        this.registrationIssueDate = registrationIssueDate;
    }

    public void registerTopik(String topikScore){
        this.topikScore = topikScore;
    }
    public void registerSocialIntegrationProgram(String socialIntegrationProgramScore){
        this.socialIntegrationProgramScore = socialIntegrationProgramScore;
    }
    public void registerSejongInstitute(String sejongInstituteScore){
        this.sejongInstituteScore = sejongInstituteScore;
    }
}
