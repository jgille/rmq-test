package org.rmqtest.junit.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.rules.ExternalResource;
import org.rmqtest.junit.Rabbit;
import org.rmqtest.junit.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class RabbitTestRule extends ExternalResource {

    private final String configPath;

    private Rabbit rabbit;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RabbitTestRule(String configPath) {
        this.configPath = configPath;
    }

    protected void before() throws IOException {
        RabbitConfig rabbitConfig =
                new ObjectMapper(new YAMLFactory()).readValue(getResource(configPath), RabbitConfig.class);

        logger.info("Initializing rabbit test with config: {}", rabbitConfig);
        this.rabbit = new Rabbit(rabbitConfig);
    }

    private URL getResource(String configPath) {
        return getClass().getClassLoader().getResource(configPath);
    }

    protected void after() {
        logger.info("Closing rabbit connection");
        rabbit.tearDown();
    }

    public Rabbit rabbit() {
        return rabbit;
    }
}
