package com.group5.best3deals.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;  // Default to Gmail if not specified

    @Value("${spring.mail.port:587}")
    private int mailPort;  // Default to 587 if not specified

    @Value("${spring.mail.protocol:smtp}")
    private String mailProtocol;  // Default to SMTP

    @Value("${spring.mail.debug:true}")
    private boolean mailDebug;  // Enable debug by default

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailProtocol);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", String.valueOf(mailDebug));

        return mailSender;
    }
}
