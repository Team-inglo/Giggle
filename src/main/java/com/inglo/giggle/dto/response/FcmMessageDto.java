package com.inglo.giggle.dto.response;

import lombok.Builder;

@Builder
public record FcmMessageDto(
        boolean validateOnly,
        MessageDto message
){}
