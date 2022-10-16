package com.marcnuri.demo.eclipsecon2022.northwind;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderService {

  public Uni<Order> findById(short id) {
    return Order
      .find("FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.orderDetails WHERE o.orderId = ?1", id)
      .firstResult();
  }

  public Uni<List<Order>> range(Integer limit, String sort, Sort.Direction direction) {
    // Using two queries to prevent:
    //   HR000104: firstResult/maxResults specified with collection fetch; applying in memory!
    final Sort orderSort = Sort.by("o." + sort, direction)
      .and("o.orderId", Sort.Direction.Descending);
    return Order
      .listIds(orderSort)
      .range(0, limit)
      .list()
      .chain(orderIds -> Order.findByIds(orderIds, orderSort));
  }

  public Uni<Long> count() {
    return Order.count();
  }

  public Uni<Long> getPendingShipmentCount() {
    return Order.getPendingShipmentCount();
  }

  public Uni<Order.Revenue> getTotalRevenue() {
    return Order.revenue();
  }
}
