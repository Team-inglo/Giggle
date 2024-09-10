package com.inglo.giggle.dto.response;

import lombok.Builder;

@Builder
public record NotificationDto(
        String title,
        String body,
        String image
){}