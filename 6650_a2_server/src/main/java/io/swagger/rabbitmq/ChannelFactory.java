package io.swagger.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChannelFactory {

    private static final Logger logger = LoggerFactory.getLogger(ChannelFactory.class);
    private static final Connection connection = getConnection();

    private static Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("54.185.77.25");
        factory.setUsername("gloria");
        factory.setPassword("123");
        try {
            Connection temp = factory.newConnection();
            logger.info("Connection created");
            return temp;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static Channel getChannel() {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
