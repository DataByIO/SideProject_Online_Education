package com.main.ioteacher.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mail_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;   // 수신자 이메일
    private String subject;     // 메일 제목

    @Column(columnDefinition = "TEXT")
    private String content;     // 본문 내용

    @Enumerated(EnumType.STRING)
    private Status status;      // SUCCESS / FAILURE

    @Column(columnDefinition = "TEXT")
    private String errorMessage; // 에러 발생 시 메시지

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        SUCCESS, FAILURE
    }
}
