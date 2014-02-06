package org.rmqtest.junit;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Publisher {
    private final String routingKey;
    private final String exchange;
    private final Channel channel;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Publisher(Channel channel, String exchange, String routingKey) {
        this.channel = channel;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(String... messages) throws IOException {
        for (String message : messages) {
            logger.info("Publishing message '{}' on exhange: {}, routing key: {}", message, exchange, routingKey);
            byte[] messageBytes = message.getBytes("UTF-8");
            channel.basicPublish(exchange, routingKey, true, null, messageBytes);
        }
    }
}
