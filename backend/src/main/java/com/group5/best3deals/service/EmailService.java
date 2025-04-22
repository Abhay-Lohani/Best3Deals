package com.group5.best3deals.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    // Method to send any email
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            emailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Error while sending email to {}: {}", to, e.getMessage());
        }
    }

    // Method to send a verification email (using the generic sendEmail method)
    public void sendVerificationEmail(String to, String subject, String text) {
        sendEmail(to, subject, text);
    }

}
