package com.main.ioteacher.user.repository;

import com.main.ioteacher.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);

    boolean existsByEmail(String email);

    // ✅ 추가: 이메일로 사용자 검색
    Optional<User> findByEmail(String email);
}
