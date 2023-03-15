package com.redhat.developers.northwind.dashboard;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntityBase {

  @Id
  @Column(name = "customer_id", nullable = false, length = 5)
  public String id;
  @Column(name = "company_name", nullable = false, length = 40)
  public String companyName;
  @Column(name = "contact_name", length = 30)
  public String contactName;
  @Column(name = "contact_title", length = 30)
  public String contactTitle;
  @Column(name = "address", length = 60)
  public String address;
  @Column(name = "city", length = 15)
  public String city;
  @Column(name = "region", length = 15)
  public String region;
  @Column(name = "postal_code", length = 10)
  public String postalCode;
  @Column(name = "country", length = 15)
  public String country;
  @Column(name = "phone", length = 24)
  public String phone;
  @Column(name = "fax", length = 24)
  public String fax;
}
