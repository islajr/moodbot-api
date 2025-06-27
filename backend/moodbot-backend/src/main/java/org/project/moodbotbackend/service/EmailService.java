package org.project.moodbotbackend.service;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.entity.EmailDetails;
import org.project.moodbotbackend.service.interfaces.EmailServiceInterface;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(EmailDetails emailDetails) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDetails.getRecipient());
            message.setSubject(emailDetails.getSubject());
            message.setText(emailDetails.getMessageBody());
            message.setSentDate(new Date());
            message.setFrom("MoodBot");

            mailSender.send(message);

        } catch (MailException e) {
            throw new RuntimeException("failed to send mail!"); // customize later?
        } catch (Exception e) {
            throw new RuntimeException("something went wrong!");    // customize later
        }
    }
}
