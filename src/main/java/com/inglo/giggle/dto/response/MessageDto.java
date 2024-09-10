package com.inglo.giggle.dto.response;

import lombok.Builder;

@Builder
public record MessageDto(
        NotificationDto notification,
        String token
){}