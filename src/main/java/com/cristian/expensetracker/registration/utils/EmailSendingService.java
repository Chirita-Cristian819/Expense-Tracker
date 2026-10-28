package com.cristian.expensetracker.registration.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailSendingService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailSendingService.class);
    @Value("${application.host}") private String host;

    public EmailSendingService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(
            String recipient,
            String verificationLinkPath) {

        String html;
        String verificationLink = host + verificationLinkPath;
        ClassPathResource template =
                new ClassPathResource("templates/email-verification.html");
        try (var input = template.getInputStream()) {
            html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read the verification email template", exception);
        }

        html = html.replace("{{verificationLink}}", HtmlUtils.htmlEscape(verificationLink));

        try {
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

            mailSender.send(message);
        } catch (MailException exception) {
            log.error("Unable to send verification email", exception);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
