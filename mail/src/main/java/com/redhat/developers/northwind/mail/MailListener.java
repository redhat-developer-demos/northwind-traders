package com.redhat.developers.northwind.mail;

import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;

@RabbitListener
public class MailListener {

  private final MailService mailService;

  public MailListener(MailService mailService) {
    this.mailService = mailService;
  }

  @Queue("${mail.queue.send}")
  public void sendMail(Message message) {
    mailService.sendMail(message);
  }
}
