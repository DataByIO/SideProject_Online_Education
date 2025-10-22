package com.main.ioteacher.user;

public enum UserStatus {
    ACTIVE,      // 정상 사용 가능
    SUSPENDED,   // 일시 정지 (관리자 제재)
    DELETED,     // 탈퇴된 계정
    PENDING      // 이메일 인증 등 승인 대기 상태 (선택)
}
