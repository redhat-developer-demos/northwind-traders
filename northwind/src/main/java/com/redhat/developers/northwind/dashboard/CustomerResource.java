package com.redhat.developers.northwind.dashboard;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
