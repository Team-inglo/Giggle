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

    @Column(name = "owner_registration_number", nullable = false)
    private String ownerRegistrationNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_address", nullable = false)
    private String storeAddress;

    @Column(name = "store_phone_number", nullable = false)
    private String storePhoneNumber;

    @Column(name = "store_email", nullable = false)
    private String storeEmail;

    @Builder
    public Owner(User user, String ownerRegistrationNumber, String ownerName, String storeName, String storeAddress, String storePhoneNumber, String storeEmail){
        this.user = user;
        this.ownerRegistrationNumber = ownerRegistrationNumber;
        this.ownerName = ownerName;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storePhoneNumber = storePhoneNumber;
        this.storeEmail = storeEmail;
    }

    public static Owner signUp(User user) {
        return Owner.builder()
                .user(user)
                .ownerRegistrationNumber("")
                .ownerName("")
                .storeName("")
                .storeAddress("")
                .storeEmail("")
                .storePhoneNumber("")
                .build();
    }
}
