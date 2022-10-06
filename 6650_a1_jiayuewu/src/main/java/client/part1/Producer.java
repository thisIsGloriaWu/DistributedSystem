package client.part1;

import common.DataGenerator;
import io.swagger.client.model.LiftRide;

import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements Runnable {
    private final LinkedBlockingQueue<Event> queue;

    private int size;

    public Producer(int size) {
        this.size = size;
        this.queue = new LinkedBlockingQueue<>();
    }

    public Event serve() {
        return this.queue.poll();
    }

    public boolean isEmpty() {
        return size == 0 && queue.isEmpty();
    }

    @Override
    public void run() {
        while (size > 0) {
            // generate a lift ride event
            Event event = new Event(
                    new LiftRide(),
                    DataGenerator.getInstance().getRandomResortId(),
                    DataGenerator.getInstance().getRandomSeasonId(),
                    DataGenerator.getInstance().getRandomDayId(),
                    DataGenerator.getInstance().getRandomSkierId());
            this.queue.add(event);
            size--;
        }
    }
}
