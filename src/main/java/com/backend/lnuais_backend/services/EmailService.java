package com.backend.lnuais_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import this
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
}