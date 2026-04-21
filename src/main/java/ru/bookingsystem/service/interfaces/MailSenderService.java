package ru.bookingsystem.service.interfaces;

public interface MailSenderService {
    void send(String to, String subject, String body);
}
