package com.inglo.giggle.service;

import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.dto.request.RequestSignatureDto;
import com.inglo.giggle.dto.request.WebClientRequestDto;
import com.inglo.giggle.dto.response.WebClientResponseDto;
import com.inglo.giggle.dto.type.DocumentType;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;

    // 시간제 취업허가서 신청
    public Long createAnnouncement(Long userId, AnnouncementCreateDto request) {
        // documentType 가져오기


        return null;
    }
}
