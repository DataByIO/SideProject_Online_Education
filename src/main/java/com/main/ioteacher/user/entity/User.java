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
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(nullable = false)
    private Boolean blacklisted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    /** ✅ 주소 관련 필드 세분화 */
    @Column(name = "road_address", length = 255)
    private String roadAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "agree_marketing")
    private Boolean agreeMarketing = false;

    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<CourseReview> reviews;
}
