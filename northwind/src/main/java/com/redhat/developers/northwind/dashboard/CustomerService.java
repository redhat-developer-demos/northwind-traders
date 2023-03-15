package com.redhat.developers.northwind.dashboard;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CustomerService {

  public Uni<List<Customer>> list() {
    return Customer.listAll();
  }

}
