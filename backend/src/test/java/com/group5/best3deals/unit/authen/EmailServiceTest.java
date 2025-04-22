package com.group5.best3deals.unit.authen;

import com.group5.best3deals.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_ShouldSendMessageSuccessfully() {
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail("test@example.com", "Test Subject", "<p>Test Content</p>");

        verify(emailSender).send(mimeMessage);
    }



    @Test
    void sendVerificationEmail_ShouldCallSendEmail() {
        EmailService spyService = spy(emailService);
        doNothing().when(spyService).sendEmail(any(), any(), any());

        spyService.sendVerificationEmail("test@example.com", "Verify", "<p>Verify</p>");

        verify(spyService).sendEmail("test@example.com", "Verify", "<p>Verify</p>");
    }
}