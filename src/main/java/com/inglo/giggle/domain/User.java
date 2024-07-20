package com.inglo.giggle.domain;

import com.inglo.giggle.constants.Constants;
import com.inglo.giggle.dto.request.AuthSignUpDto;
import com.inglo.giggle.dto.type.EProvider;
import com.inglo.giggle.dto.type.ERole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

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
    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "status_of_residence")
    private String statusOfResidence;

    @Column(name = "period_of_stay")
    private String periodOfStay;

    @Column(name = "passport_issue_date")
    private String passportIssueDate;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "registration_issue_date")
    private String registrationIssueDate;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "topik_score")
    private String topikScore;

    @Column(name = "social_integration_program_score")
    private String socialIntegrationProgramScore;

    @Column(name = "sejong_institute_score")
    private String sejongInstituteScore;

    /* User Status */
    @Column(name = "is_login", columnDefinition = "TINYINT(1)")
    private Boolean isLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder
    public User(String serialId, String password, EProvider provider, ERole role,
                String name, String address, String passportNumber, String statusOfResidence,
                String periodOfStay, String passportIssueDate,
                String nationality, String registrationIssueDate, String registrationNumber) {
        this.serialId = serialId;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.name = name;
        this.address = address;
        this.passportNumber = passportNumber;
        this.statusOfResidence = statusOfResidence;
        this.periodOfStay = periodOfStay;
        this.passportIssueDate = passportIssueDate;
        this.nationality = nationality;
        this.registrationIssueDate = registrationIssueDate;
        this.registrationNumber = registrationNumber;
        this.createdAt = LocalDateTime.now();
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
    public static User signUp(String serialId, EProvider provider) {
        return User.builder()
                .serialId(serialId)
                .provider(provider)
                .password(null)
                .role(ERole.USER)
                .build();
    }

}
