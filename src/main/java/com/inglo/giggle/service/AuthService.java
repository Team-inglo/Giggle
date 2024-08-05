package com.inglo.giggle.service;

import com.inglo.giggle.domain.User;
import com.inglo.giggle.domain.UserFile;
import com.inglo.giggle.dto.request.AuthSignUpDto;
import com.inglo.giggle.dto.response.JwtTokenDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.UserFileRepository;
import com.inglo.giggle.repository.UserRepository;
import com.inglo.giggle.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkDuplicate(String email) {
        return userRepository.existsBySerialId(email);
    }

    @Transactional
    public void signUp(AuthSignUpDto authSignUpDto) { // 아이디 비밀번호 등록 시, User와 UserFile을 생성
        User user = userRepository.save(
                User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()))
        );
        userFileRepository.save(UserFile.signUp(user));
    }

    @Transactional
    public JwtTokenDto reissue(Long userId, String refreshToken) {
        User user = userRepository.findByIdAndRefreshTokenAndIsLogin(userId, refreshToken, true)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_LOGIN_USER));

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        return jwtTokenDto;
    }
}
