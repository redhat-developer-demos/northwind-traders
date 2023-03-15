package com.redhat.developers.northwind.mail;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.micronaut.context.annotation.Value;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class MailChannelPool extends ChannelInitializer {

  @Value("${mail.exchange.send}")
  protected String mailSendExchange;
  @Value("${mail.queue.send}")
  protected String mailSendQueue;

  @Override
  public void initialize(Channel channel, String name) throws IOException {
    channel.exchangeDeclare(mailSendExchange, BuiltinExchangeType.FANOUT, true);
    channel.queueDeclare(mailSendQueue,true, false, false, null);
    channel.queueBind(mailSendQueue, mailSendExchange, "");
  }
}
