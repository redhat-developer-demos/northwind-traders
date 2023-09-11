package com.redhat.developers.northwind.orderfaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderFakerApplication {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(OrderFakerApplication.class, args)));
  }
}
