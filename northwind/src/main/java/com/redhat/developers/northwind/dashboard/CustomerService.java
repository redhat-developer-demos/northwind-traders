package com.redhat.developers.northwind.dashboard;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CustomerService {

  @WithSession
  public Uni<List<Customer>> list() {
    return Customer.listAll();
  }

}
