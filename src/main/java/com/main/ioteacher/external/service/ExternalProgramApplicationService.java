package com.main.ioteacher.external.service;

import com.main.ioteacher.common.MailService;
import com.main.ioteacher.external.entity.ExternalProgram;
import com.main.ioteacher.external.entity.ExternalProgramApplication;
import com.main.ioteacher.external.repository.ExternalProgramApplicationRepository;
import com.main.ioteacher.external.repository.ExternalProgramRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExternalProgramApplicationService {

    private final ExternalProgramApplicationRepository applicationRepository;
    private final ExternalProgramRepository programRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private static final String UPLOAD_DIR = "/Users/kimnoah/IdeaProjects/ioteacher/uploads/applications/";

    public ExternalProgramApplication saveApplication(
            Long programId,
            String userId,
            boolean privacyAgreed,
            String name,
            String phone,
            String email,
            String organization,
            String position,
            Integer experience,
            String introduction,
            String careerSummary,
            String preferredField,
            String referralSource,
            MultipartFile identityFile,
            MultipartFile employmentFile
    ) {
        try {
            // ✅ FK 객체 로드
            ExternalProgram program = programRepository.findById(programId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다: " + programId));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다: " + userId));

            // ✅ 파일 저장
            String identityPath = saveFile(identityFile);
            String employmentPath = saveFile(employmentFile);

            // ✅ 엔티티 빌드
            ExternalProgramApplication app = ExternalProgramApplication.builder()
                    .program(program)       // ✅ programId 대신 program 객체 설정
                    .user(user)             // ✅ userId 대신 user 객체 설정
                    .privacyAgreed(privacyAgreed)
                    .name(name)
                    .phone(phone)
                    .email(email)
                    .organization(organization)
                    .position(position)
                    .experienceYears(experience)
                    .introduction(introduction)
                    .careerSummary(careerSummary)
                    .preferredField(preferredField)
                    .referralSource(referralSource)
                    .identityFilePath(identityPath)
                    .employmentFilePath(employmentPath)
                    .status(ExternalProgramApplication.Status.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            ExternalProgramApplication saved = applicationRepository.save(app);

            // ✅ 관리자에게 이메일 알림
            mailService.sendApplicationNotice(
                    "ro_bot__@naver.com",
                    name,
                    program.getTitle().toString()  // ✅ 프로그램 이름 표시
            );

            return saved;

        } catch (Exception e) {
            throw new RuntimeException("신청 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        file.transferTo(new File(filePath));

        return "/uploads/applications/" + fileName;
    }
}
