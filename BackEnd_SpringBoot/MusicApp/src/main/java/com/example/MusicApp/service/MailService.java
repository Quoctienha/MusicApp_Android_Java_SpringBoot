package com.example.MusicApp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void send(String to, String subject, String username, String link) {
        try {
            // Thiết lập context cho template
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("link", link);

            // Tạo nội dung email từ template Thymeleaf
            String htmlContent = templateEngine.process("email-template", context);

            // Tạo MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("appmusic112@gmail.com");

<<<<<<< Updated upstream
            // Gửi email
=======
            mailSender.send(message);
            System.out.println("Verification email sent to " + to);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send verification email", e);
        }
    }

    // ✅ For password reset using a codr
    public void sendResetCodeEmail(String to, String subject, String username, String code) {
        try {
            System.out.println("Sending reset code to: " + to);
            System.out.println("Code: " + code);

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("code", code); // Must match th:text="${code}" in your HTML

            String htmlContent = templateEngine.process("email-reset-code", context); // Use correct template

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("appmusic112@gmail.com");

>>>>>>> Stashed changes
            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);

        } catch (MessagingException e) {
            throw new IllegalStateException("Gửi email thất bại", e);
        }
    }
}
