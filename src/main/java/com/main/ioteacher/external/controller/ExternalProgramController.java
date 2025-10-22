package com.main.ioteacher.external.controller;

import com.main.ioteacher.external.entity.ExternalProgramResponse;
import com.main.ioteacher.external.service.ExternalProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external-programs")
@RequiredArgsConstructor
public class ExternalProgramController {

    private final ExternalProgramService externalProgramService;

    // 전체 조회
    @GetMapping
    public List<ExternalProgramResponse> getAll(@RequestParam(defaultValue = "ko") String lang) {
        return externalProgramService.getAllPrograms(lang);
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ExternalProgramResponse getById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ko") String lang
    ) {
        return externalProgramService.getProgramById(id, lang);
    }

    // 상태별 필터 조회
    @GetMapping("/status/{status}")
    public List<ExternalProgramResponse> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "ko") String lang
    ) {
        return externalProgramService.getProgramsByStatus(status, lang);
    }
}
