package client.part1;

import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestClient implements Runnable{

    private static final int SIZE = 100;
    private static Producer producer = new Producer(SIZE);
    private static final String BASE_PATH = "http://34.220.218.12:8080/swagger-spring";

    private static final TestClient client = new TestClient();

    private static AtomicLong sum = new AtomicLong(0);

    public static void main(String[] args) {
        Thread produceThread = new Thread(producer);
        Thread clientThread = new Thread(client);
        long totalStart = System.currentTimeMillis();
        produceThread.start();
        clientThread.start();
        try {
            produceThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long totalEnd = System.currentTimeMillis();
        System.out.println("Actual Throughput: " + String.format("%.2f", SIZE*1000.0/(totalEnd-totalStart)));
        System.out.println("Theoretical Throughput: " + String.format("%.2f", 1000.0 * SIZE/sum.get()));
    }

    @Override
    public void run() {
        while (!producer.isEmpty()) {
            SkiersApi apiInstance = new SkiersApi();
            apiInstance.getApiClient().setBasePath(BASE_PATH);
            Event event = producer.serve();
            if (event == null) {
                continue;
            }
            sendRequest(apiInstance, event);
            System.out.println("sent");
        }
    }

    private int sendRequest(SkiersApi apiInstance, Event event) {
        for (int i = 0; i < 5; i++) {
            try {
                long start = System.currentTimeMillis();
                apiInstance.writeNewLiftRideWithHttpInfo(
                        event.getRide(),
                        event.getResortId(),
                        event.getSeasonId(),
                        event.getDayId(),
                        event.getSkierId());
                long end = System.currentTimeMillis();
                sum.getAndAdd(end-start);
                return 1;
            } catch (ApiException e) {
                int code = e.getCode() / 100;
                if (code != 4 && code != 5) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
