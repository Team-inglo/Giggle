package com.inglo.giggle.domain;

import com.inglo.giggle.dto.request.UpdateOwnerDto;
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

    @Column(name = "store_address_name")
    private String storeAddressName;

    @Column(name = "store_address_x")
    private Float storeAddressX;

    @Column(name = "store_address_y")
    private Float storeAddressY;

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

    public void updateOwner(UpdateOwnerDto updateOwnerDto) {
        if(updateOwnerDto.ownerRegistrationNumber() != null) {
            this.ownerRegistrationNumber = updateOwnerDto.ownerRegistrationNumber();
        }
        if(updateOwnerDto.ownerName() != null) {
            this.ownerName = updateOwnerDto.ownerName();
        }
        if(updateOwnerDto.storeName() != null) {
            this.storeName = updateOwnerDto.storeName();
        }
        if(updateOwnerDto.storeAddressName() != null) {
            this.storeAddressName = updateOwnerDto.storeAddressName();
        }
        if(updateOwnerDto.storeAddressX() != null) {
            this.storeAddressX = updateOwnerDto.storeAddressX();
        }
        if(updateOwnerDto.storeAddressY() != null) {
            this.storeAddressY = updateOwnerDto.storeAddressY();
        }
        if(updateOwnerDto.storePhoneNumber() != null) {
            this.storePhoneNumber = updateOwnerDto.storePhoneNumber();
        }
        if(updateOwnerDto.storeEmail() != null) {
            this.storeEmail = updateOwnerDto.storeEmail();
        }
    }
}
