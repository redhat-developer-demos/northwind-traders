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
  @Value("${mail.to}")
  protected String to;
  private final EmailSender<?, ?> emailSender;

  @Inject
  public MailService(EmailSender<?, ?> emailSender) {
    this.emailSender = emailSender;
  }

  public void sendMail(String message) {
    emailSender.send(Email.builder()
        .from(from)
        .to(to)
        .subject("New Entry processed")
        .body(message));
  }
}
