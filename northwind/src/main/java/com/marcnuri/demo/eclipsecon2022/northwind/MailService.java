package com.marcnuri.demo.eclipsecon2022.northwind;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MailService {

  private final OrderService orderService;
  private final Emitter<String> mailSend;

  @Inject
  public MailService(OrderService orderService, @Channel("mail-send") Emitter<String> mailSend) {
    this.orderService = orderService;
    this.mailSend = mailSend;
  }

  public void send(String to, String subject, String body) {
    mailSend.send(Message.of(body));
  }

  public void sendOrder(short orderId) {
    orderService.findById(orderId).subscribe().with(order ->
      send("", "", "The message " + order.customer.companyName));
  }
}
