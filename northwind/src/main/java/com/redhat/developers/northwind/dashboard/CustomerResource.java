package com.redhat.developers.northwind.dashboard;

import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/api/v1/customers")
public class CustomerResource {

  private final CustomerService customerService;

  @Inject
  public CustomerResource(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GET
  public Uni<List<Customer>> list() {
    return customerService.list();
  }

}
