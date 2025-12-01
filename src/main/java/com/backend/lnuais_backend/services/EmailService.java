package com.backend.lnuais_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Inject the email from application.properties so it's dynamic
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // Use the real sender
            message.setTo(toEmail);
            message.setSubject("LNU AI Society - Verify Your Account");
            message.setText("Welcome to the LNU AI Society!\n\n" +
                    "Your verification code is: " + code + "\n\n" +
                    "Please enter this code on the website to complete your registration.");

            mailSender.send(message);
            System.out.println(" Email sent successfully to " + toEmail);

        } catch (Exception e) {
            System.out.println(" Error sending email: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("LNU AI Society - Password Reset Code");
            message.setText("Hello,\n\n" +
                    "You have requested to reset your password.\n" +
                    "Your reset code is: " + code + "\n\n" +
                    "This code will expire in 15 minutes.\n" +
                    "If you did not request this, please ignore this email.");

            mailSender.send(message);
            System.out.println(" Password reset email sent to " + toEmail);
        } catch (Exception e) {
            System.out.println(" Error sending reset email: " + e.getMessage());
        }
    }
}