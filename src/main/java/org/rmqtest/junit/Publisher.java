package org.rmqtest.junit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Publisher {
    private final String routingKey;
    private final String exchange;
    private final Connection connection;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Publisher(Connection connection, String exchange, String routingKey) {
        this.connection = connection;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(String... messages) throws IOException {
        Channel channel = null;
        try {
            channel = connection.createChannel();
            for (String message : messages) {
                logger.info("Publishing message '{}' on exhange: {}, routing key: {}", message, exchange, routingKey);
                byte[] messageBytes = message.getBytes("UTF-8");
                channel.basicPublish(exchange, routingKey, true, null, messageBytes);
            }
        } finally {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        }
    }
}
