package com.redhat.developers.northwind.dashboard;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.stream.Collectors;

@ApplicationScoped
public class MailService {

  private final OrderService orderService;
  private final Emitter<Email> mailSend;

  @Inject
  public MailService(OrderService orderService, @Channel("mail-send") Emitter<Email> mailSend) {
    this.orderService = orderService;
    this.mailSend = mailSend;
  }

  public void send(String to, String subject, String body) {
    mailSend.send(Message.of(new Email(to, subject, body)));
  }

  @WithSession
  public Uni<Order> sendOrder(short orderId) {
    return orderService.findById(orderId).map(order -> {
      final String subject = "Order " + orderId + " must be shipped";
      final String orderDetails = order.orderDetails.stream()
        // Padding must be aligned with the details section of the EMAIL_TEMPLATE
        .map(od -> String.format("%-8s", od.productId) + "| " + od.quantity).collect(Collectors.joining("\n"))
        // Hack for proper rendering in Mailinator
        .replace(" ", "\u00A0");
      final String message = String.format(EMAIL_TEMPLATE,
        order.orderId, order.customer.companyName,
        order.shipName, order.shipAddress, order.shipPostalCode, order.shipCity, order.shipCountry,
        orderDetails);
      send("northwind-warehouse@mailinator.com", subject, message);
      return order;
    });
  }

  record Email(String to, String subject, String message) {
  }

  private static final String EMAIL_TEMPLATE = """
    ORDER: %s
    CUSTOMER: %s
    SHIP TO:
      %s
      %s
      %s - %s (%s)
    
    ==================
    PRODUCT | QUANTITY
    ==================
    %s
    """;
}
