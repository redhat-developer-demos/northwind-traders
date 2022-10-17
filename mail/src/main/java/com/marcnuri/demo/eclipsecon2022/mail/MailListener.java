package com.marcnuri.demo.eclipsecon2022.mail;

import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;

@RabbitListener
public class MailListener {

  private final MailService mailService;

  public MailListener(MailService mailService) {
    this.mailService = mailService;
  }

  @Queue("${mail.queue.send}")
  public void sendMail(String message) {
    mailService.sendMail(message);
  }
}
