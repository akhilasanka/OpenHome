package com.cmpe275.openhome.notification;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotification {
    final Session session;

    public EmailNotification(final Session session) {
        this.session = session;
    }

    public void sendEmail(String email, final String subject, final String body) {
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
    }
}
