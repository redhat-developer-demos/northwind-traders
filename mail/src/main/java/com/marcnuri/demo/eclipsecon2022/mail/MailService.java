package com.marcnuri.demo.eclipsecon2022.mail;

import io.micronaut.context.annotation.Value;
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MailService {

  @Value("${mail.from}")
  protected String from;
  private final EmailSender<?, ?> emailSender;

  @Inject
  public MailService(EmailSender<?, ?> emailSender) {
    this.emailSender = emailSender;
  }

  public void sendMail(Message message) {
    emailSender.send(Email.builder()
        .from(from)
        .to(message.to())
        .subject(message.subject())
        .body(
          "<html><body><pre>" + message.message() + "</pre></body></html>",
          message.message()));
  }
}
