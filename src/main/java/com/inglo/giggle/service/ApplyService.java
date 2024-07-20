package com.inglo.giggle.service;

import com.inglo.giggle.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyService {
    private final ApplyRepository applyRepository;
}
