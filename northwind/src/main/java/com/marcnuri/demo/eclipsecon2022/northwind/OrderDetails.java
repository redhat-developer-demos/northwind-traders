package com.marcnuri.demo.eclipsecon2022.northwind;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@JsonIdentityInfo(property = "serialId", generator = ObjectIdGenerators.PropertyGenerator.class)
public class OrderDetails extends PanacheEntityBase implements Serializable {
  @Id
  @Column(name = "order_id")
  public short orderId;
  @Id
  @Column(name = "product_id")
  public short productId;
  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "order_id")
  public Order order;
  @Column(name = "unit_price")
  public BigDecimal unitPrice;
  @Column(name = "quantity")
  public short quantity;
  @Column(name = "discount")
  public BigDecimal discount;

  public String getSerialId() {
    return orderId + "-" + productId;
  }
}
