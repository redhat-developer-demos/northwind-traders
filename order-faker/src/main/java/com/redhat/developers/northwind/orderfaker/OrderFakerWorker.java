package com.redhat.developers.northwind.orderfaker;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class OrderFakerWorker {

  private final JobRepository jobRepository;
  private final Random random;

  @Value("${northwind.url}")
  String northwindUrl;

  @Autowired
  public OrderFakerWorker(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
    random = new Random();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.rootUri(northwindUrl).build();
  }

  @Bean
  public Tasklet postFakeOrder(RestTemplate restTemplate) {
    return (contribution, chunkContext) -> {
      final var headers = new HttpHeaders();
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      final var customers = restTemplate
        .exchange("/api/v1/customers", GET, new HttpEntity<>(headers), Order.Customer[].class)
        .getBody();
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
    };
  }

  @Bean
  public Step orderFakerStep1(Tasklet postFakeOrder, PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("postFakeOrder", jobRepository)
      .tasklet(postFakeOrder, platformTransactionManager)
      .build();
  }

  @Bean
  public Job orderFakerJob(Step orderFakerStep1) {
    return new JobBuilder("orderFakerJob", jobRepository)
      .flow(orderFakerStep1).build()
      .build();
  }
}
