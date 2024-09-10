package com.inglo.giggle.dto.request;

public record FcmRequestDto(
        String targetToken,
        String title,
        String body
){
}