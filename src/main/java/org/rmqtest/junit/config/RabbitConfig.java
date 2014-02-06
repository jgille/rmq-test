package org.rmqtest.junit.config;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class RabbitConfig {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000;

    public String host = ConnectionFactory.DEFAULT_HOST;
    public String vHost = ConnectionFactory.DEFAULT_VHOST;
    public int port = ConnectionFactory.DEFAULT_AMQP_PORT;
    public String username = ConnectionFactory.DEFAULT_USER;
    public String password = ConnectionFactory.DEFAULT_PASS;
    public int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    public List<QueueConfig> queues = new ArrayList<>();
    public List<DestinationConfig> destinations = new ArrayList<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
