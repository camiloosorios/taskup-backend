package com.uptask.api.Services.helpers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String name, String token) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom("UpTask <admin@uptask.com>");
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);
            String htmlTemplate = readFile("emailTemplate.html");
            String htmlContent = htmlTemplate.replace("${user}", name);
            htmlContent = htmlContent.replace("${token}", token);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public String readFile(String filepath) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + filepath);

        return Files.readString(resource.getFile().toPath());
    }
}