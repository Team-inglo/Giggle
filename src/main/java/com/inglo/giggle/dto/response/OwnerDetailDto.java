package com.inglo.giggle.dto.response;

import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "OwnerDetailDto", description = "사업자(점주) 상세 정보 조회 Dto")
public record OwnerDetailDto(
        Long userId,
        String ownerRegistrationNumber,
        String ownerName,
        String storeName,
        String storeAddressName,
        Float storeAddressX,
        Float storeAddressY
) implements UserDetailDto {
    public static OwnerDetailDto fromEntity(User user, Owner owner) {
        return OwnerDetailDto.builder()
                .userId(user.getId())
                .ownerRegistrationNumber(owner.getOwnerRegistrationNumber())
                .ownerName(owner.getOwnerName())
                .storeName(owner.getStoreName())
                .storeAddressName(owner.getStoreAddressName())
                .storeAddressX(owner.getStoreAddressX())
                .storeAddressY(owner.getStoreAddressY())
                .build();
    }
}
