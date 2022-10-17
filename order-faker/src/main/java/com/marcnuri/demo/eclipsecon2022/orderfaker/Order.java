package com.marcnuri.demo.eclipsecon2022.orderfaker;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record Order(
  Customer customer,
  Short employeeId,
  Date orderDate,
  Date requiredDate,
  Date shippedDate,
  Short shipVia,
  Float freight,
  String shipName,
  String shipAddress,
  String shipCity,
  String shipRegion,
  String shipPostalCode,
  String shipCountry,
  List<OrderDetails> orderDetails
) {
  public record Customer(
    String id,
    String companyName,
    String contactName,
    String address,
    String city,
    String region,
    String postalCode,
    String country
  ) {
  }

  public record OrderDetails(
    short productId,
    BigDecimal unitPrice,
    short quantity,
    BigDecimal discount
  ) {
  }
}
