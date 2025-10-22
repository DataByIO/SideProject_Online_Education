package com.main.ioteacher.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendApplicationNotice(String to, String applicantName, String programTitle) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setFrom("ro_bot__@naver.com");  // ✅ 인증 계정으로 반드시 지정!
        helper.setSubject("[IOTeacher] 새로운 외부교육 신청이 접수되었습니다");
        helper.setText(String.format("""
                안녕하세요, 관리자님 👋

                새로운 외부교육 신청이 접수되었습니다.

                📘 교육 프로그램: %s
                👤 신청자: %s

                관리자 페이지에서 신청 내역을 확인해 주세요.
                """, programTitle, applicantName), false);

        mailSender.send(message);
    }
}
