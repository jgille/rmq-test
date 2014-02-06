package org.rmqtest.junit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.rmqtest.junit.config.DestinationConfig;
import org.rmqtest.junit.config.QueueConfig;
import org.rmqtest.junit.config.RabbitConfig;

import java.io.IOException;

public class Rabbit {

    private final RabbitConfig rabbitConfig;
    private final Connection connection;
    private final Channel channel;

    public Rabbit(RabbitConfig config) throws IOException {
        this.rabbitConfig = config;
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.host);
        connectionFactory.setPort(config.port);
        connectionFactory.setVirtualHost(config.vHost);
        connectionFactory.setUsername(config.username);
        connectionFactory.setPassword(config.password);
        connectionFactory.setConnectionTimeout(config.connectionTimeout);
        this.connection = connectionFactory.newConnection();
        this.channel = connection.createChannel();
    }

    public Consumer consumer(String alias) {
        String queue = null;
        for (QueueConfig queueConfig : rabbitConfig.queues) {
            if (alias.equalsIgnoreCase(queueConfig.alias)) {
                queue = queueConfig.queueName;
                break;
            }
        }
        if (queue == null) {
            throw new IllegalArgumentException("No queue configured with alias: " + alias);
        }
        return new Consumer(channel, queue);
    }

    public Publisher publisher(String alias) {
        DestinationConfig destinationConfig = null;
        for (DestinationConfig destination : rabbitConfig.destinations) {
            if (alias.equalsIgnoreCase(destination.alias)) {
                destinationConfig = destination;
                break;
            }
        }
        if (destinationConfig == null) {
            throw new IllegalArgumentException("No destination configured with alias: " + alias);
        }
        return new Publisher(channel, destinationConfig.exchange, destinationConfig.routingKey);
    }

    public void tearDown() {
        try {
            if (channel.isOpen()) {
                channel.close();
            }
            if (connection.isOpen()) {
                connection.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to close rabbit connection", e);
        }
    }
}
