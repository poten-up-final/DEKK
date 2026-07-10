package com.dekk.app.admin.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendInviteEmail(String toEmail, String inviteToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[DEKK] 백오피스 관리자 계정 초대");

            String inviteUrl = "https://space.dekk.co.kr/signup?token=" + inviteToken;

            message.setText("안녕하세요.\n\n" + "DEKK 백오피스 관리자로 초대되었습니다.\n"
                    + "아래 링크를 클릭하여 비밀번호 및 소속을 입력하고 가입을 완료해 주세요.\n\n"
                    + inviteUrl
                    + "\n\n" + "※ 본 링크는 24시간 동안만 유효합니다.");

            mailSender.send(message);
            log.info("[AdminEmailService] 초대 메일 발송 완료. To: {}", toEmail);

        } catch (Exception e) {
            log.error("[AdminEmailService] 초대 메일 발송 실패. To: {}", toEmail, e);
        }
    }
}
