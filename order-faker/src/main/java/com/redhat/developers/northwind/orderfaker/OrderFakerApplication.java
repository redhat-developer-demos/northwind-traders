package com.redhat.developers.northwind.orderfaker;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class OrderFakerApplication {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(OrderFakerApplication.class, args)));
  }
}
