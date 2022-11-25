package consumer;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import io.swagger.model.LiftRide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbitmq.RemoteQueue;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerMain.class);
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final String REDIS_IP = "54.186.10.209";

    public static void main(String[] args) {
        RemoteQueue queue = new RemoteQueue(1);

        // connect to Redis
        RedisClient redisClient = new RedisClient(
                RedisURI.create(String.format("redis://%s:6379", REDIS_IP))
        );
        RedisConnection<String, String> connection = redisClient.connect();
        LOGGER.info("Connected to Redis.");

        queue.handleMessages((s, delivery) -> {
            try (ObjectInputStream objectReader = new ObjectInputStream(new ByteArrayInputStream(delivery.getBody()))) {
                Map<String, String> userInfo = new HashMap<>();

                String skierId = "user:" + objectReader.readObject().toString();
                String resortId = objectReader.readObject().toString();
                LiftRide ride = (LiftRide) objectReader.readObject();

                userInfo.put("resortId", resortId);
                userInfo.put("liftId", String.valueOf(ride.getLiftID()));

                // write data into Redis
                String result = connection.hmset(skierId, userInfo);
                if (result.equals("OK")) {
                    LOGGER.info(String.format("Received %d messages", counter.addAndGet(1)));
                } else {
                    LOGGER.info("Write Redis info failed: " + result);
                }
                ;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
