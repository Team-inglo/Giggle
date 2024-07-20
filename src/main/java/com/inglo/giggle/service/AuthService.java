package com.inglo.giggle.service;

import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.request.AuthSignUpDto;
import com.inglo.giggle.dto.request.OauthLoginDto;
import com.inglo.giggle.dto.response.JwtTokenDto;
import com.inglo.giggle.dto.type.ERole;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.UserRepository;
import com.inglo.giggle.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkDuplicate(String email) {
        return userRepository.existsBySerialId(email);
    }

    public void signUp(AuthSignUpDto authSignUpDto) {
        userRepository.save(
                User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()))
        );
    }
    @Transactional
    public JwtTokenDto login(OauthLoginDto userLoginDto) {
        User user;
        boolean isNewUser = false;

        Optional<User> existingUser = userRepository.findBySerialId(userLoginDto.serialId());

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = userRepository.save(User.signUp(userLoginDto.serialId(), userLoginDto.provider()));
            isNewUser = true;
        }

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), ERole.USER);

        if (isNewUser || !jwtTokenDto.refreshToken().equals(user.getRefreshToken())) {
            userRepository.updateRefreshTokenAndLoginStatus(user.getId(), jwtTokenDto.refreshToken(), true);
        }

        return jwtTokenDto;
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
