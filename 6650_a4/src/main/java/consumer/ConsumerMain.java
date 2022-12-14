package consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbitmq.RemoteQueue;
import redis.RedisTemplate;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class ConsumerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerMain.class);

    private static final Integer THREADS = 64;

    public static void main(String[] args) {
        RemoteQueue queue = new RemoteQueue(THREADS);
        RedisTemplate redis = new RedisTemplate();

        for (int i = 0; i < THREADS; i++) {
            queue.handleMessages((s, delivery) -> {
                try (ByteArrayInputStream bytes = new ByteArrayInputStream(delivery.getBody()); ObjectInputStream objectReader = new ObjectInputStream(bytes)) {
                    String skierId = objectReader.readObject().toString();
                    String resortId = objectReader.readObject().toString();
                    String seasonId = objectReader.readObject().toString();
                    String dayId = objectReader.readObject().toString();
                    Integer liftId = (Integer) objectReader.readObject();

                    redis.writeEntry(skierId, resortId, seasonId, dayId, liftId);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
