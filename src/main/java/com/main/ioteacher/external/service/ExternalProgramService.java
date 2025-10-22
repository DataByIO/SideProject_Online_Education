package com.main.ioteacher.external.service;

import com.main.ioteacher.external.entity.ExternalProgram;
import com.main.ioteacher.external.entity.ExternalProgramResponse;
import com.main.ioteacher.external.repository.ExternalProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalProgramService {

    private final ExternalProgramRepository externalProgramRepository;

    /**
     * 모든 프로그램 조회
     */
    public List<ExternalProgramResponse> getAllPrograms(String lang) {
        return externalProgramRepository.findAll().stream()
                .map(program -> ExternalProgramResponse.from(program, lang))
                .toList();
    }

    /**
     * 단일 프로그램 조회
     */
    public ExternalProgramResponse getProgramById(Long id, String lang) {
        ExternalProgram program = externalProgramRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));
        return ExternalProgramResponse.from(program, lang);
    }

    /**
     * 상태별 필터 (UPCOMING / CLOSED)
     */
    public List<ExternalProgramResponse> getProgramsByStatus(String status, String lang) {
        return externalProgramRepository.findAll().stream()
                .filter(p -> p.getStatus() != null && p.getStatus().name().equalsIgnoreCase(status))
                .map(p -> ExternalProgramResponse.from(p, lang))
                .toList();
    }
}
