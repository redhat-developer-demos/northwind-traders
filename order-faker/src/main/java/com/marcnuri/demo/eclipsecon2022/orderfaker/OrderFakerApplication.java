package com.marcnuri.demo.eclipsecon2022.orderfaker;

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
