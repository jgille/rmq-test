package org.rmqtest.junit.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class QueueConfig {

    public String alias;

    public String queueName;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
