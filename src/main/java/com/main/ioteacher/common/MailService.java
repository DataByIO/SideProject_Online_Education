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

    /** ✅ 외부교육 신청 알림 메일 */
    public void sendApplicationNotice(String to, String applicantName, String programTitle) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setFrom("osaeknabi2022@naver.com");
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

    /** ✅ 회원가입 이메일 인증 코드 발송 */
    public void sendVerificationCode(String to, String code, boolean isEmailAvailable) {
        if (!isEmailAvailable) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다. 다른 이메일을 사용하세요.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setFrom("osaeknabi2022@naver.com");
            helper.setSubject("[IOTeacher] 이메일 인증 코드");
            helper.setText(String.format("""
                    안녕하세요 👋

                    IOTeacher 회원가입을 위한 이메일 인증 코드입니다.

                    🔐 인증코드: %s

                    본 코드는 10분 동안만 유효합니다.
                    """, code), false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    /** ✅ 비밀번호 재설정용 인증 메일 */
    public void sendResetPasswordMail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setFrom("osaeknabi2022@naver.com");
            helper.setSubject("[IOTeacher] 비밀번호 재설정 인증코드");
            helper.setText(String.format("""
                    안녕하세요 👋

                    아래 인증코드를 입력하여 비밀번호를 재설정하세요.

                    🔐 인증코드: %s

                    본 코드는 10분 동안 유효합니다.
                    """, code), false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("비밀번호 재설정 메일 발송 실패", e);
        }
    }

    /** ✅ 비밀번호 변경 완료 알림 메일 */
    public void sendPasswordResetSuccessMail(String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setFrom("osaeknabi2022@naver.com");
            helper.setSubject("[IOTeacher] 비밀번호 변경 안내");
            helper.setText("""
                    안녕하세요 👋

                    요청하신 비밀번호 변경이 성공적으로 완료되었습니다.
                    본인이 직접 요청하지 않았다면 즉시 관리자에게 문의해주세요.

                    🔒 보안을 위해 정기적으로 비밀번호를 변경하는 것을 권장드립니다.

                    감사합니다.
                    """, false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("비밀번호 변경 알림 메일 발송 실패", e);
        }
    }

    /** ✅ 문의 폼 메일 발송 */
    public void sendContactMail(String name, String email, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setTo("osaeknabi2022@naver.com"); // 수신자 (관리자)
        helper.setFrom("osaeknabi2022@naver.com");
        helper.setSubject("[IOTeacher 문의] " + subject);

        String body = String.format("""
            📩 새로운 문의가 접수되었습니다.

            👤 이름: %s
            📧 이메일: %s

            💬 메시지:
            %s
            """, name, email, message);

        helper.setText(body, false);
        mailSender.send(mimeMessage);
    }
}
