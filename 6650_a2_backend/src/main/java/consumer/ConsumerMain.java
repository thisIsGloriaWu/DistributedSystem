package consumer;

import io.swagger.model.LiftRide;
import rabbitmq.RemoteQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerMain.class);
    private static final ConcurrentMap<Integer, List<LiftRide>> skierRides = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        RemoteQueue queue = new RemoteQueue(1);
        queue.handleMessages((s, delivery) -> {
            ByteArrayInputStream bytes = new ByteArrayInputStream(delivery.getBody());
            ObjectInputStream objectReader = new ObjectInputStream(bytes);
            int skierId = bytes.read();
            LiftRide ride = null;
            try {
                ride = (LiftRide) objectReader.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (!skierRides.containsKey(skierId)) {
                skierRides.put(skierId, new ArrayList<>());
            }
            skierRides.get(skierId).add(ride);
            LOGGER.info(String.format("Received %d messages", counter.addAndGet(1)));
        });
    }
}
