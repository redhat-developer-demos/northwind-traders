package com.marcnuri.demo.eclipsecon2022.northwind;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@JsonIdentityInfo(property = "orderId", generator = ObjectIdGenerators.PropertyGenerator.class)
public class Order extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id", nullable = false)
  public short orderId;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  public Customer customer;
  @Column(name = "employee_id")
  public Short employeeId;
  @Column(name = "order_date")
  public Date orderDate;
  @Column(name = "required_date")
  public Date requiredDate;
  @Column(name = "shipped_date")
  public Date shippedDate;
  @Column(name = "ship_via")
  public Short shipVia;
  @Column(name = "freight")
  public Float freight;
  @Column(name = "ship_name", length = 40)
  public String shipName;
  @Column(name = "ship_address", length = 60)
  public String shipAddress;
  @Column(name = "ship_city", length = 15)
  public String shipCity;
  @Column(name = "ship_region", length = 15)
  public String shipRegion;
  @Column(name = "ship_postal_code", length = 10)
  public String shipPostalCode;
  @Column(name = "ship_country", length = 15)
  public String shipCountry;
  @OneToMany(mappedBy = "order")
  public List<OrderDetails> orderDetails;

  public static PanacheQuery<PanacheEntityBase> listIds(Sort sort) {
    return find("SELECT o.id FROM Order o", sort);
  }

  public static Uni<List<Order>> findByIds(Object orderIds, Sort sort) {
    return find(
      "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails LEFT JOIN FETCH o.customer " +
        "WHERE o.id IN (:orderIds)",
      sort,
      Parameters.with("orderIds", orderIds)
    ).list();
  }

  public static Uni<Long> getPendingShipmentCount() {
    return count("shippedDate IS NULL");
  }

  public static Uni<Revenue> revenue() {
    return find(
      "SELECT COUNT(distinct od.order) AS orders, SUM(od.unitPrice * od.quantity) AS revenue " +
        "FROM OrderDetails od")
      .project(Revenue.class)
      .firstResult();
  }

  static class Revenue {
    final long orderCount;
    final BigDecimal totalRevenue;

    public Revenue(long orderCount, BigDecimal totalRevenue) {
      this.orderCount = orderCount;
      this.totalRevenue = totalRevenue;
    }

    public long getOrderCount() {
      return orderCount;
    }

    public BigDecimal getTotalRevenue() {
      return totalRevenue;
    }
  }
}
