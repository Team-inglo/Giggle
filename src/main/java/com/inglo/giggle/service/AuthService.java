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

    @Transactional
    public boolean checkDuplicate(String serialId) { // 아이디 중복 검사. 단, 아이디가 존재하더라도 그 계정의 데이터가 비어있다면 삭제하고 중복이 아닌 것으로 간주
        User user = userRepository.findBySerialId(serialId).orElse(null);
        // 중복 안되는 경우
        if (user == null) return true;
        // 중복인데 그 계정의 데이터가 비어있는 경우
        else {
            if (user.getRole() == ERole.APPLICANT) {
                Applicant applicant = applicantRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
                // 유학생이고 회원가입을 끝까지 안한경우
                if (applicant.getRegistrationNumber() == null) {
                    applicantFileRepository.deleteByApplicant(applicant);
                    applicantRepository.delete(applicant);
                    userRepository.delete(user);
                    return true;
                }
            }
            // 점주고 회원가입을 끝까지 안한경우
            if (user.getRole() == ERole.OWNER) {
                Owner owner = ownerRepository.findByUser(user).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
                if (owner.getStoreName() == null) {
                    ownerRepository.delete(owner);
                    userRepository.delete(user);
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public JwtTokenDto signUp(AuthSignUpDto authSignUpDto) { // 아이디 비밀번호 등록 시, User와 UserFile을 생성
        ERole role = ERole.fromName(authSignUpDto.role());
        User user;
        if (role == ERole.APPLICANT) {
            user = userRepository.save(
                    User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()), role)
            );
            Applicant applicant = applicantRepository.save(Applicant.signUp(user));
            applicantFileRepository.save(ApplicantFile.signUp(applicant));
        }
        else {
            user = userRepository.save(
                    User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()), role)
            );
            ownerRepository.save(Owner.signUp(user));
        }
        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());
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
