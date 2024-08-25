package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateOwnerDto(
        @JsonProperty("registration_number")
        String ownerRegistrationNumber,
        @JsonProperty("owner_name")
        String ownerName,
        @JsonProperty("store_name")
        String storeName,
        @JsonProperty("store_address_name")
        String storeAddressName,
        @JsonProperty("store_address_x")
        Float storeAddressX,
        @JsonProperty("store_address_y")
        Float storeAddressY,
        @JsonProperty("store_phone_number")
        String storePhoneNumber,
        @JsonProperty("store_email")
        String storeEmail
) {
}
