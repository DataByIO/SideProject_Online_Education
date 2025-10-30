package com.main.ioteacher.common;

import com.main.ioteacher.common.entity.ContactRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    /** ✅ 문의 메일 전송 API */
    @PostMapping("/contact")
    public ResponseEntity<String> sendContactMail(@RequestBody ContactRequest request) {
        try {
            mailService.sendContactMail(request.getName(), request.getEmail(), request.getSubject(), request.getMessage());
            return ResponseEntity.ok("메일 전송 완료");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("메일 전송 실패: " + e.getMessage());
        }
    }
}
