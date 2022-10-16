package com.marcnuri.demo.eclipsecon2022.northwind;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/api/v1")
public class OrderResource {

  private final OrderService orderService;

  @Inject
  public OrderResource(OrderService orderService) {
    this.orderService = orderService;
  }

  @GET
  @Path("/orders")
  public Uni<List<Order>> list(
    @QueryParam("limit") Integer limit, @QueryParam("sort") String sort, @QueryParam("direction") Sort.Direction direction) {
    return orderService.range(limit, sort, direction);
  }

  @GET
  @Path("/orders/count")
  public Uni<Long> count(@QueryParam("status") String status) {
    if ("pending-shipment".equals(status)) {
      return orderService.getPendingShipmentCount();
    }
    return orderService.count();
  }

  @GET
  @Path("/revenue")
  public Uni<Order.Revenue> getTotalRevenue() {
    return orderService.getTotalRevenue();
  }

}
