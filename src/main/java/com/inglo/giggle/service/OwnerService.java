package com.inglo.giggle.service;

import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.dto.request.UpdateOwnerDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.OwnerRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;

    @Transactional
    public void updateOwner(Long userId, UpdateOwnerDto updateOwnerDto) {
        Owner owner = ownerRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
        owner.updateOwner(updateOwnerDto);
    }
}
