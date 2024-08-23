package com.inglo.giggle.controller;

import com.inglo.giggle.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final OwnerService ownerService;
}
