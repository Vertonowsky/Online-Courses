package com.example.hello_world.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmailService {


    private JavaMailSender javaMailSender;

    @Autowired
    public void setMailDependency(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kursowo-pl@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }


    public void sendVerificationEmail(String to, String subject, String htmlFilePath) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("kursowo-pl@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        File file = new File(htmlFilePath);
        Path path = Paths.get(file.toURI());
        String htmlContent = Files.readString(path);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }
}