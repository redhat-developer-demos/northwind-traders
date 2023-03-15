package com.redhat.developers.northwind.dashboard;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
