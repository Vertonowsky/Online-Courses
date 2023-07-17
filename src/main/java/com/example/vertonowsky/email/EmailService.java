package com.example.vertonowsky.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {


    private JavaMailSender javaMailSender;
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kursowo-pl@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }


    public void sendHtmlEmail(String to, String subject, HashMap<String, Object> variables, String htmlFilePath) {

        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom("pomoc@kursowo.pl");
                helper.setTo(to);
                helper.setSubject(subject);

                TemplateEngine templateEngine = new SpringTemplateEngine();
                ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
                templateResolver.setTemplateMode("HTML");
                templateResolver.setCharacterEncoding("UTF-8");
                templateEngine.setTemplateResolver(templateResolver);

                Context context = new Context();
                context.setVariables(variables);
                String htmlContent = templateEngine.process(htmlFilePath, context);

                helper.setText(htmlContent, true);

                javaMailSender.send(message);
            } catch (MessagingException e) {
                System.out.println(e.getMessage());
            }
        }, taskExecutor);
    }
}