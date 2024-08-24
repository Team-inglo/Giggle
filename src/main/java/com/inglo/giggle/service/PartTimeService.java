package com.inglo.giggle.service;

import com.inglo.giggle.domain.PartTime;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.request.PartTimeCreateDto;
import com.inglo.giggle.dto.response.PartTimeDto;
import com.inglo.giggle.dto.response.PartTimeListDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.PartTimeRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartTimeService {
    private final PartTimeRepository partTimeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPartTime(Long userId, PartTimeCreateDto partTimeCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        try {
            partTimeRepository.save(
                    PartTime.builder()
                            .partTimeName(partTimeCreateDto.name())
                            .hourlyRate(partTimeCreateDto.hourlyRate())
                            .color(partTimeCreateDto.color())
                            .user(user)
                            .build()
            );
        } catch(Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<PartTimeListDto> getPartTimes(Long userId) {
        return partTimeRepository.findAllByUserId(userId).stream()
                .map(PartTimeListDto::fromEntity)
                .toList();
    }
    public PartTimeDto getPartTime(Long partTimeId) {
        PartTime partTime = partTimeRepository.findById(partTimeId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        return PartTimeDto.fromEntity(partTime);
    }
    @Transactional
    public void updatePartTime(Long partTimeId, PartTimeCreateDto partTimeCreateDto) {
        PartTime partTime = partTimeRepository.findById(partTimeId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        try {
            partTime.updatePartTime(partTimeCreateDto.name(), partTimeCreateDto.hourlyRate(), partTimeCreateDto.color());
        } catch(Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @Transactional
    public void deletePartTime(Long partTimeId) {
        PartTime partTime = partTimeRepository.findById(partTimeId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        try {
            partTimeRepository.delete(partTime);
        } catch(Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
