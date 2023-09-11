package com.redhat.developers.northwind.dashboard;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
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

  @POST
  @Path("/orders")
  public Uni<Order> create(Order order) {
    return orderService.newOrder(order);
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
