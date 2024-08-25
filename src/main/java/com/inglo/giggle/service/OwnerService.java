package com.inglo.giggle.service;

import com.inglo.giggle.dto.request.AnnouncementCreateDto;
import com.inglo.giggle.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
