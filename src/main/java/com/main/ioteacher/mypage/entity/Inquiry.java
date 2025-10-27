package com.main.ioteacher.mypage.entity;

import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Inquiry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String type;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String status;
    private String adminReply;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
