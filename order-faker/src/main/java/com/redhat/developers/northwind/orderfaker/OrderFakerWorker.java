package com.redhat.developers.northwind.orderfaker;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.POST;

@Component
public class OrderFakerWorker {

  private final JobBuilderFactory jobBuilders;
  private final StepBuilderFactory stepBuilders;
  private final Random random;

  @Value("${northwind.url}")
  String northwindUrl;

  @Autowired
  public OrderFakerWorker(JobBuilderFactory jobBuilders, StepBuilderFactory stepBuilders) {
    this.jobBuilders = jobBuilders;
    this.stepBuilders = stepBuilders;
    random = new Random();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.rootUri(northwindUrl).build();
  }

  @Bean
  public Step postFakeOrder(RestTemplate restTemplate) {
    return stepBuilders.get("postFakeOrder")
      .tasklet((contribution, chunkContext) -> {
        final var customers = restTemplate.getForObject("/api/v1/customers", Order.Customer[].class);
        final var customer = customers[random.ints(0, customers.length).findFirst().getAsInt()];
        final var order = new Order(
          customer,
          (short) 1,
          new Date(),
          null,
          null,
          (short) 1,
          2.5F,
          customer.companyName(),
          customer.address(),
          customer.city(),
          customer.region(),
          customer.postalCode(),
          customer.country(),
          Collections.singletonList(new Order.OrderDetails(
            (short) random.ints(1, 78).findFirst().getAsInt(),
            BigDecimal.valueOf(random.doubles(0D, 100D).findFirst().getAsDouble()),
            (short) random.ints(1, 101).findFirst().getAsInt(),
            BigDecimal.ZERO))
        );
        restTemplate
          .exchange("/api/v1/orders", POST, new HttpEntity<>(order), Order.class);
        TimeUnit.SECONDS.sleep(6);
        return RepeatStatus.CONTINUABLE;
      })
      .build();
  }

  @Bean
  public Job orderFakerJob(Step postFakeOrder) {
    return jobBuilders.get("orderFakerJob")
      .flow(postFakeOrder).build()
      .build();
  }
}
