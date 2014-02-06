package org.rmqtest.junit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

public class Consumer {

    private final Channel channel;
    private final String queue;

    private int numExpectedMessages = 1;
    private long timeout = -1;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Consumer(Channel channel, String queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public Consumer drain() throws IOException {
        logger.info("Draining queue: {}", queue);
        while (true) {
            GetResponse getResponse = channel.basicGet(queue, true);
            if (getResponse == null) {
                return this;
            }
            logger.info("Message drained");
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
        final List<byte[]> messages = new ArrayList<>(numExpectedMessages);
        await(errorMessage + ". Expected " + numExpectedMessages + " messages.")
                .atMost(timeout, timeUnit)
                .until(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws IOException {
                        GetResponse getResponse = channel.basicGet(queue, true);
                        if (getResponse == null) {
                            return false;
                        }
                        byte[] body = getResponse.getBody();
                        messages.add(body);
                        logger.info("Message consumed. Total number of consumed messages: {}.", messages.size());
                        return messages.size() == numExpectedMessages;
                    }
                });
        return messages;
    }
}
