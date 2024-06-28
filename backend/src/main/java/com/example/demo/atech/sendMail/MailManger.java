package com.example.demo.atech.sendMail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.example.demo.atech.MyUtil.logErr;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailManger {
    private final JavaMailSender ms;

    /**
     * 비밀번호 설정 후 6개월이 지난 사용자에게 비밀번호 변경 안내 메일 발송
     *
     * @param to 수신인 이메일
     */
    public void recommendPwUpdate(String to, String userId) {
        MimeMessage msg = ms.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setSubject("[주차행정시스템] 비밀번호 변경 안내");
            helper.setTo(to);
            // TODO: 메시지 바디. html 적용 가능.
            String body = userId + " 계정의 비밀번호가 변경된 지 6개월이 지났음을 알려드립니다. 보안을 위해 비밀번호를 변경하시길 권장합니다.";
            helper.setText(body, true);
            // 발송
            ms.send(msg);
        } catch (MessagingException e) {
            log.error("비밀번호 변경 안내 메일 발송 중 오류 발생했습니다.", e);
        }

    }


    /**
     * 비밀번호 분실 시 임시 비밀번호 발급
     *
     * @param to    수신인 이메일
     * @param encPw 암호화 되기 전 비밀번호 원문
     */
    public void sendMail4TmpPw(String to, String encPw) {
        MimeMessage msg = ms.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setSubject("[주차행정시스템] 임시 비밀번호 발급 메일입니다.");
            helper.setTo(to);
            String emailBody =
                    "임시 비밀번호: <b style=\"color: red;\">" + encPw + "</b><br>";
            helper.setText(emailBody, true);

            ms.send(msg);
        } catch (MessagingException e) {
            logErr(e);
            throw new RuntimeException("임시 비밀번호 발급 중 오류가 발생했습니다.");
        }

    }


}
