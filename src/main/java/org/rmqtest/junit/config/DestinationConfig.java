package org.rmqtest.junit.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DestinationConfig {

    public String alias;

    public String exchange;

    public String routingKey;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
