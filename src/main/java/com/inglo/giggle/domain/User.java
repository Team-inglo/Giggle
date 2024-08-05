package com.inglo.giggle.domain;

import com.inglo.giggle.annotation.Date;
import com.inglo.giggle.dto.request.AuthSignUpDto;
import com.inglo.giggle.dto.request.UpdateUserDto;
import com.inglo.giggle.dto.type.EProvider;
import com.inglo.giggle.dto.type.ERole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "users")
public class User {
    /* Default Column */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "serial_id", nullable = false, unique = true)
    private String serialId;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private EProvider provider;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ERole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /* User Info */
    @Column(name = "address")
    private String address="";

    @Column(name = "gpa")
    private String gpa="0";

    @Column(name = "startDay")
    @Date
    private LocalDateTime startDay= null;

    @Column(name = "endDay")
    @Date
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

    /* User Status */
    @Column(name = "is_login", columnDefinition = "TINYINT(1)")
    private Boolean isLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder
    public User(String serialId, String password, EProvider provider, ERole role,
                String address, String passportNumber, String name, String sex,
                String dateOfBirth, String nationality, String passportIssueDate,
                String passportExpiryDate, String registrationNumber, String statusOfResidence,
                String registrationIssueDate, String topikScore, String socialIntegrationProgramScore,
                String sejongInstituteScore) {
        this.serialId = serialId;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.address = address;
        this.passportNumber = passportNumber;
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.passportIssueDate = passportIssueDate;
        this.passportExpiryDate = passportExpiryDate;
        this.registrationNumber = registrationNumber;
        this.statusOfResidence = statusOfResidence;
        this.registrationIssueDate = registrationIssueDate;
        this.topikScore = topikScore;
        this.socialIntegrationProgramScore = socialIntegrationProgramScore;
        this.sejongInstituteScore = sejongInstituteScore;
        this.isLogin = false;
    }
    public void updateSpec(String topikScore, String socialIntegrationProgramScore, String sejongInstituteScore) {
        this.topikScore = topikScore;
        this.socialIntegrationProgramScore = socialIntegrationProgramScore;
        this.sejongInstituteScore = sejongInstituteScore;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static User signUp(AuthSignUpDto authSignUpDto, String encodedPassword) {
        return User.builder()
                .serialId(authSignUpDto.serialId())
                .password(encodedPassword)
                .provider(EProvider.DEFAULT)
                .role(ERole.USER)
                .build();
    }
    public void updateUser(UpdateUserDto updateUserDto) {
        this.address = updateUserDto.address();
        this.gpa = updateUserDto.gpa();
        this.startDay = updateUserDto.startDay();
        this.endDay = updateUserDto.endDay();
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
