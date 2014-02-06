package org.rmqtest.examples;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rmqtest.junit.Consumer;
import org.rmqtest.junit.Publisher;
import org.rmqtest.junit.Rabbit;
import org.rmqtest.junit.categories.IntegrationTest;
import org.rmqtest.junit.rules.RabbitTestRule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Category({IntegrationTest.class})
public class ExampleTest {

    @Rule
    public RabbitTestRule rabbitTestRule = new RabbitTestRule("examples/test-rabbit.yml");

    private Publisher publisher;
    private Consumer consumer;

    @Before
    public void init() throws IOException {
        Rabbit rabbit = rabbitTestRule.rabbit();
        this.publisher = rabbit.publisher("testDestination");
        this.consumer = rabbit.consumer("testQueue").drain();
    }

    @Test
    public void assertThatTwoPublishedMessagesAreConsumed() throws IOException {
        publisher.publish("Hello", "world");

        List<byte[]> messages =
                consumer.awaitAtMost(1, TimeUnit.SECONDS)
                        .expectNumMessages(2)
                        .collect("Timed out waiting for my messages 'Hello' and 'world'");

        assertThat(messages, hasSize(2));
        assertThat(str(messages.get(0)), is("Hello"));
        assertThat(str(messages.get(1)), is("world"));
    }

    private String str(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }
}
