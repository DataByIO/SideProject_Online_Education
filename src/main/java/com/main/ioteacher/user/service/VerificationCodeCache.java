package com.main.ioteacher.user.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ✅ 인증코드 캐시
 * - 회원가입 및 비밀번호 재설정 공용 사용 가능
 * - TTL(만료 시간) 10분
 */
@Component
public class VerificationCodeCache {

    private final Map<String, VerificationEntry> cache = new ConcurrentHashMap<>();

    /** ✅ 코드 저장 (기본 TTL 10분) */
    public void store(String email, String code) {
        cache.put(email, new VerificationEntry(code, LocalDateTime.now().plusMinutes(10)));
    }

    /** ✅ 코드 검증 및 만료처리 */
    public boolean verify(String email, String code) {
        VerificationEntry entry = cache.get(email);
        if (entry == null) return false;

        // TTL 확인
        if (LocalDateTime.now().isAfter(entry.expireAt)) {
            cache.remove(email);
            return false;
        }

        boolean valid = entry.code.equals(code);
        if (valid) cache.remove(email); // ✅ 1회용 사용 후 제거
        return valid;
    }

    /** ✅ 특정 이메일의 코드 제거 (수동 만료처리) */
    public void invalidate(String email) {
        cache.remove(email);
    }

    /** ✅ 내부 클래스 */
    private static class VerificationEntry {
        String code;
        LocalDateTime expireAt;
        VerificationEntry(String code, LocalDateTime expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }
}
