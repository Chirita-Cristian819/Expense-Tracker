package com.cristian.expensetracker.emailsendrequests.utils;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Getter
public class EmailSender {

    private final JavaMailSender mailSender;
    private final CircuitBreaker emailCircuitBreaker;

    @Value("${application.host}") private String host;

    public EmailSender(JavaMailSender mailSender,
                       CircuitBreaker emailCircuitBreaker) {
        this.mailSender = mailSender;
        this.emailCircuitBreaker = emailCircuitBreaker;
    }

    /**
     * @param recipient
     * @param verificationLinkPath
     * @throws IOException
     * @throws MessagingException
     * @throws MailException
     * Defines the email configuration parameters and sends email
     **/
    public String sendVerificationEmail(
            String recipient,
            String verificationLinkPath) throws IOException, MessagingException, MailException {

        String html;
        String verificationLink = host + verificationLinkPath;
        ClassPathResource template =
                new ClassPathResource("templates/email-verification.html");
        try (var input = template.getInputStream()) {
            html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }

        html = html.replace("{{verificationLink}}", HtmlUtils.htmlEscape(verificationLink));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(recipient);
        helper.setSubject("Verify your Pocket Tracer account");
        helper.setText(
                "Confirm your Pocket Tracer email address using this link:\n" + verificationLink
                        + "\n\nIf you didn't create this account, you can ignore this email.",
                html);
        helper.addInline("pocket-tracer-logo",
                new ClassPathResource("mail/pocket-tracer-logo.png"), "image/png");

        emailCircuitBreaker.executeRunnable(
                () -> mailSender.send(message)
        );
        return message.getMessageID();
    }

}
