package com.cristian.expensetracker.emailsendrequests.utils;

import com.cristian.expensetracker.emailsendrequests.EmailSendRequestRepository;
import com.cristian.expensetracker.emailsendrequests.EmailSendStatus;
import jakarta.mail.*;
import jakarta.mail.internet.InternetHeaders;
import jakarta.mail.search.FromStringTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Properties;

@Service
public class IMAPMessagesRead {

    private static final Logger log = LoggerFactory.getLogger(IMAPMessagesRead.class);

    @Value("${app.imap.host}") private String host;
    @Value("${app.imap.port}") private int port;
    @Value("${app.imap.protocol}") private String protocol;
    @Value("${app.imap.username}") private String username;
    @Value("${app.imap.password}") private String password;
    @Value("${app.imap.folder}") private String folder;
    @Value("${app.imap.processed-folder}") private String processedFolder;

    private final MailProperties mailProperties;
    private final EmailSendRequestRepository emailSendRequestRepository;

    public IMAPMessagesRead(MailProperties mailProperties,
                            EmailSendRequestRepository emailSendRequestRepository) {
        this.mailProperties = mailProperties;
        this.emailSendRequestRepository = emailSendRequestRepository;
    }

    /*
     * Reads bounce reports from the inbox, marks the matching requests as BOUNCED
     * and moves the reports to a separate folder, so each run only reads new ones.
     */
    @Scheduled(fixedDelayString = "${app.imap.poll-interval}")
    @Transactional
    public void readBouncedMessages() {
        // IMAP timeouts and SSL settings come from spring.mail.properties[mail.imaps.*]
        Properties properties = new Properties();
        properties.putAll(mailProperties.getProperties());

        try (Store store = Session.getInstance(properties).getStore(protocol)) {
            store.connect(host, port, username, password);
            Folder processed = store.getFolder(processedFolder);
            if (!processed.exists()) {
                processed.create(Folder.HOLDS_MESSAGES);
            }

            try (Folder inbox = store.getFolder(folder)) {
                inbox.open(Folder.READ_WRITE);

                for (Message report : inbox.search(new FromStringTerm("mailer-daemon"))) {
                    try {
                        processReport(report);
                        inbox.copyMessages(new Message[]{report}, processed);
                        report.setFlag(Flags.Flag.DELETED, true);
                    } catch (MessagingException | IOException | RuntimeException exception) {
                        log.error("Unable to process bounce report, retried on the next run", exception);
                    }
                }
                inbox.expunge();
            }
        } catch (MessagingException exception) {
            log.error("Unable to read bounce reports from the IMAP server", exception);
        }

        emailSendRequestRepository.markAsVerified(
                EmailSendStatus.SENT, EmailSendStatus.SENT_VERIFIED,
                OffsetDateTime.now(ZoneOffset.UTC).minusHours(72));
    }

    private void processReport(Part part) throws MessagingException, IOException {
        if (!part.isMimeType("multipart/*")) {
            return;
        }

        String originalId = null;
        boolean failed = false;
        Multipart content = (Multipart) part.getContent();

        for (int i = 0; i < content.getCount(); i++) {
            BodyPart bodyPart = content.getBodyPart(i);

            if (bodyPart.isMimeType("message/delivery-status")) {
                // "delayed" reports are not bounces: delivery may still succeed
                failed = new String(bodyPart.getInputStream().readAllBytes()).contains("Action: failed");
            } else if (bodyPart.isMimeType("text/rfc822-headers")) {
                originalId = new InternetHeaders(bodyPart.getInputStream()).getHeader("Message-ID", null);
            } else if (bodyPart.isMimeType("message/rfc822")) {
                String[] ids = ((Message) bodyPart.getContent()).getHeader("Message-ID");
                originalId = ids != null ? ids[0] : null;
            }
        }

        if (failed && originalId != null) {
            emailSendRequestRepository.findByMessageId(originalId.trim())
                    .filter(request -> request.getStatus() == EmailSendStatus.SENT
                            || request.getStatus() == EmailSendStatus.SENT_VERIFIED)
                    .ifPresent(request -> {
                        request.setStatus(EmailSendStatus.BOUNCED);
                        emailSendRequestRepository.save(request);
                    });
        }
    }
}