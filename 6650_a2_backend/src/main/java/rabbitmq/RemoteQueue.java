package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.swagger.model.LiftRide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RemoteQueue implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteQueue.class);
    private static final String HOST = "35.89.185.143";
    private static final String QUEUE_NAME = "a2_messages";

    private Connection connection = null;
    private final BlockingQueue<Channel> channels;

    public RemoteQueue(int poolSize) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername("gloria");
        factory.setPassword("123");
        LOGGER.info("Attempting to connect to " + HOST);
        try {
            connection = factory.newConnection();
            LOGGER.info("Rabbitmq connection established");
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Rabbitmq connection failed");
            throw new RuntimeException(e);
        }
        channels = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            channels.add(createChannel());
        }
        LOGGER.info(String.format("Channel pool established, size(%d)", poolSize));
    }

    @Override
    public void close() throws Exception {
        if (connection == null) {
            return;
        }
        if (connection.isOpen()) {
            connection.close();
        }
        connection = null;
    }

    private Channel createChannel() {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            return channel;
        } catch (IOException e) {
            LOGGER.error("Failed to create channel");
            throw new RuntimeException(e);
        }
    }

    public void postMessage(byte[] bytes) {
        try {
            // Take from pool
            Channel channel = channels.take();
            channel.basicPublish("", QUEUE_NAME, null, bytes);
            LOGGER.info("Posted message");
            // Return to pool
            channels.add(channel);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Failed to post message");
            throw new RuntimeException(e);
        }
    }

    public void postMessage(int skierId, LiftRide ride) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ObjectOutputStream objectWriter = new ObjectOutputStream(bytes)) {
            bytes.write(skierId);
            objectWriter.writeObject(ride);
            postMessage(bytes.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleMessages(DeliverCallback callback) {
        try {
            // Taken and never returned
            Channel channel = channels.take();
            channel.basicConsume(QUEUE_NAME, true, callback, s -> {
            });
            LOGGER.info("Successfully registered callback");
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Failed to register callback");
            throw new RuntimeException(e);
        }
    }
}
