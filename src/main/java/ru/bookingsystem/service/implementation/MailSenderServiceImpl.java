package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.bookingsystem.service.interfaces.MailSenderService;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${WEBAPP_LINK}")
    private String link;



    @Override
    public void send(String to, String subject, String body){

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);
    }

    @Override
    public void sendActivationCode(String to, String username, String code){

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to Booking System. Please visit next link: %s/api/activate/%s",
                username,
                link,
                code);

        send(to, "Activation", message);
    }
}
