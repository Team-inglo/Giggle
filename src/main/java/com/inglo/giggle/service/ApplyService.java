package com.inglo.giggle.service;

import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.response.ApplyLogDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.ApplyRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final UserRepository userRepository;

    // document log 조회 api
    public ApplyLogDto getDocumentLog(Long userId) {
        // User로 Apply 엔티티 리스트 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<Apply> applies = applyRepository.findByUser(user);

        List<ApplyLogDto.DocumentSpec> documentSpecs = applies.stream()
                .map(apply -> new ApplyLogDto.DocumentSpec(
                        apply.getAnnouncement().getTitle(),
                        apply.getCreatedAt().toLocalDate(),
                        apply.getStep(),
                        ""
                ))
                .toList();

        return ApplyLogDto.builder()
                .logs(documentSpecs)
                .build();
    }
}
