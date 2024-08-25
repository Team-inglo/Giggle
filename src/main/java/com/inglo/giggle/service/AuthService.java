package com.inglo.giggle.service;

import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.domain.ApplicantFile;
import com.inglo.giggle.dto.request.AuthSignUpDto;
import com.inglo.giggle.dto.response.JwtTokenDto;
import com.inglo.giggle.dto.type.ERole;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.ApplicantFileRepository;
import com.inglo.giggle.repository.ApplicantRepository;
import com.inglo.giggle.repository.OwnerRepository;
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
    private final ApplicantRepository applicantRepository;
    private final OwnerRepository ownerRepository;
    private final ApplicantFileRepository applicantFileRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkDuplicate(String email) {
        return userRepository.existsBySerialId(email);
    }

    @Transactional
    public void signUp(AuthSignUpDto authSignUpDto) { // 아이디 비밀번호 등록 시, User와 UserFile을 생성
        ERole role = ERole.fromName(authSignUpDto.role());

        if (role == ERole.APPLICANT) {
            User user = userRepository.save(
                    User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()), role)
            );
            Applicant applicant = applicantRepository.save(Applicant.signUp(user));
            applicantFileRepository.save(ApplicantFile.signUp(applicant));
        }
        else {
            User user = userRepository.save(
                    User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()), role)
            );
            ownerRepository.save(Owner.signUp(user));
        }
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
