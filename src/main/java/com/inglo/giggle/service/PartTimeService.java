package com.inglo.giggle.service;

import com.inglo.giggle.repository.PartTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartTimeService {
    private final PartTimeRepository partTimeRepository;
}
