package com.redhat.developers.northwind.dashboard;

import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/v1/mail")
public class MailResource {

  private final MailService mailService;

  @Inject
  public MailResource(MailService mailService) {
    this.mailService = mailService;
  }

  @POST
  @Path("/orders/{orderId}")
  public Uni<Order> sendOrder(@PathParam("orderId") short orderId) {
    return mailService.sendOrder(orderId);
  }
}
