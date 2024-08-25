package com.inglo.giggle.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "owner")
public class Owner {
    @Id
    @Column(name = "owner_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @Column(name = "owner_registration_number")
    private String ownerRegistrationNumber;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_address")
    private String storeAddress;

    @Column(name = "store_phone_number")
    private String storePhoneNumber;

    @Column(name = "store_email")
    private String storeEmail;

    @Builder
    public Owner(User user){
        this.user = user;
    }

    public static Owner signUp(User user) {
        return Owner.builder()
                .user(user)
                .build();
    }
}
