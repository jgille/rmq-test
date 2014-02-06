package org.rmqtest.junit;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Consumer {

    private final Connection connection;
    private final String queue;

    private int numExpectedMessages = 1;
    private long timeout = -1;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Consumer(Connection connection, String queue) {
        this.connection = connection;
        this.queue = queue;
    }

    public Consumer drain() throws IOException {
        logger.info("Draining queue: {}", queue);
        Channel channel = null;
        try {
            channel = connection.createChannel();
            while (true) {
                GetResponse getResponse = channel.basicGet(queue, true);
                if (getResponse == null) {
                    return this;
                }
                logger.info("Message drained");
            }
        } finally {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        }
    }

    public Consumer expectNumMessages(int numMessages) {
        this.numExpectedMessages = numMessages;
        return this;
    }

    public Consumer awaitAtMost(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.timeUnit = unit;
        return this;
    }

    public List<byte[]> collect(String errorMessage) throws IOException {
        logger.info("Attempting to consume {} messages from {}", numExpectedMessages, queue);
        final CountDownLatch latch = new CountDownLatch(numExpectedMessages);
        final List<byte[]> messages = new ArrayList<>(numExpectedMessages);

        Channel channel = null;
        try {
            channel = connection.createChannel();
            channel.basicConsume(queue, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    messages.add(body);
                    latch.countDown();
                }
            });
            try {
                latch.await(timeout, timeUnit);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        }

        if (messages.size() < numExpectedMessages) {
            throw new AssertionError(errorMessage + " Expected " + numExpectedMessages + " messages but " +
                    "only got " + messages.size());
        }
        return messages;
    }
}
