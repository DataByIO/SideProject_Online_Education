package com.main.ioteacher.user.repository;

import com.main.ioteacher.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // ✅ 아이디(이메일) 찾기: 이름 + 전화번호
    Optional<User> findByNameAndPhone(String name, String phone);
}
