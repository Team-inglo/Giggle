package com.inglo.giggle.service;

import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.response.UserDetailDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.UserRepository;
import com.inglo.giggle.utility.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailDto readUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserDetailDto.fromEntity(user);
    }

    @Transactional
    public void updateUser(Long userId) {
        // TODO: OCR 사용하여 자격증 파일 읽어와, 자격증 정보를 등록하고, 유저 정보 업데이트
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));



    }
}
