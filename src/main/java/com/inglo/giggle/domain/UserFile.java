package com.inglo.giggle.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_file")
public class UserFile {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "passport_file_url")
    private String passportFileUrl;

    @Column(name = "registration_file_url")
    private String registrationFileUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public UserFile(User user, String passportFileUrl, String registrationFileUrl) {
        this.user = user;
        this.passportFileUrl = passportFileUrl;
        this.registrationFileUrl = registrationFileUrl;
        this.createdAt = LocalDateTime.now();
    }
}
