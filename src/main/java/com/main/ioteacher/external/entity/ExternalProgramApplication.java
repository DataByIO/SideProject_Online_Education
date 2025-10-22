package com.main.ioteacher.external.entity;

import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "external_program_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ✅ builder() 메서드 자동 생성
public class ExternalProgramApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    /** 외부 교육 프로그램 (FK: external_programs.program_id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private ExternalProgram program;

    /** 신청자 (FK: users.user_id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean privacyAgreed;

    private String name;
    private String phone;
    private String email;
    private String organization;
    private String position;
    private Integer experienceYears;

    @Column(columnDefinition = "text")
    private String introduction;

    @Column(columnDefinition = "text")
    private String careerSummary;

    @Column(columnDefinition = "text")
    private String preferredField;

    @Column(columnDefinition = "text")
    private String referralSource;

    private String identityFilePath;
    private String employmentFilePath;

    /** 신청 상태 */
    @Enumerated(EnumType.STRING)
    @Builder.Default  // ✅ builder 사용 시 기본값 유지
    private Status status = Status.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** ✅ 생성/수정 시각 자동 관리 */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** 신청 상태 ENUM */
    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
