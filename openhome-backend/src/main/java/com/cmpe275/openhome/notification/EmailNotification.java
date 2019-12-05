package com.cmpe275.openhome.notification;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class EmailNotification {
    final Session session;
    final ExecutorService executorService;

    public EmailNotification(final Session session) {
        this.session = session;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void sendEmail(String email, final String subject, final String body) {
        long startTime = System.nanoTime();
        executorService.submit(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("openhomecmpe275@gmail.com"));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(email)
                );
                message.setSubject("OpenHome: " + subject);
                message.setText(body);
                Transport.send(message);
                System.out.println("Done sending message.");
            } catch (MessagingException e) {
                System.err.println("Failed to send message. : ");
                e.printStackTrace();
            }

        });
        long endTime = System.nanoTime();
        System.out.println("Email Notification (in ms) took " + (endTime - startTime)/1000 );

    }
}
