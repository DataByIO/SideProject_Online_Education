package com.main.ioteacher.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.main.ioteacher.course.entity.CourseReview;
import com.main.ioteacher.user.Role;
import com.main.ioteacher.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"password", "reviews", "comments", "posts"})
//(Entity) → DB 테이블과 직접 매핑, 내부 시스템에서 사용
public class User {

    // ✅ PK는 문자열 userId
    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;    // 로그인용 이메일

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;   // ✅ com.main.ioteacher.user.Role

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(nullable = false)
    private Boolean blacklisted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken; // JWT Refresh Token

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<CourseReview> reviews;


}
