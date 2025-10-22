package com.main.ioteacher.external.controller;

import com.main.ioteacher.external.entity.ExternalProgramApplication;
import com.main.ioteacher.external.service.ExternalProgramApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/external-programs/applications")
@RequiredArgsConstructor
public class ExternalProgramApplicationController {

    private final ExternalProgramApplicationService applicationService;

    /**
     * ✅ 외부교육 신청 등록
     * - 파일 업로드 (신분증/재직증명서)
     * - DB 저장
     * - 관리자 이메일 알림
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ExternalProgramApplication> submitApplication(
            @RequestParam("programId") Long programId,
            @RequestParam("userId") String userId,
            @RequestParam("privacyAgreed") boolean privacyAgreed,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("organization") String organization,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "experience", required = false) Integer experience,
            @RequestParam("introduction") String introduction,
            @RequestParam("careerSummary") String careerSummary,
            @RequestParam("preferredField") String preferredField,
            @RequestParam("referralSource") String referralSource,
            @RequestPart(value = "identityFile", required = false) MultipartFile identityFile,
            @RequestPart(value = "employmentFile", required = false) MultipartFile employmentFile
    ) {
        ExternalProgramApplication saved = applicationService.saveApplication(
                programId,
                userId,
                privacyAgreed,
                name,
                phone,
                email,
                organization,
                position,
                experience,
                introduction,
                careerSummary,
                preferredField,
                referralSource,
                identityFile,
                employmentFile
        );

        return ResponseEntity.ok(saved);
    }
}
