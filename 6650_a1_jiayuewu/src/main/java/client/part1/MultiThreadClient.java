package client.part1;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadClient {
    private final static int THREAD_NUM = 32;
    private final static int SIZE = 200000;
    private final static int RETRY = 5;
    private final static int RESET_THREAD_NUM = 64;
    private final static int TIMES = 100;
    private static final AtomicBoolean isCompleted = new AtomicBoolean(false);
    private static final AtomicInteger totalSent = new AtomicInteger(0);
    private final static String BASE_PATH = "http://34.214.3.202:8080/swagger-spring";
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_NUM);

    public static void main(String[] args) {

        final Producer producer = new Producer(SIZE);
        final MultiThreadClient client = new MultiThreadClient();
        Thread produceThread = new Thread(producer);
        long start = System.currentTimeMillis();
        // Set the producer max thread priority
        produceThread.setPriority(Thread.MAX_PRIORITY);
        produceThread.start();

        for (int i = 0; i < THREAD_NUM; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    int sent = 0;
                    for (int i = 0; i < 1000 && !Thread.interrupted(); i++) {
                        // send POST requests
                        SkiersApi apiInstance = new SkiersApi();
                        apiInstance.getApiClient().setBasePath(BASE_PATH);
                        Event event = producer.serve();
                        sent += client.sendRequest(apiInstance, event);
                        if (sent >= 100) {
                            totalSent.addAndGet(sent);
                            sent = 0;
                        }
                    }
                    isCompleted.set(true);
                    totalSent.addAndGet(sent);
                }
            });
        }

        // Wait for a thread to finish its tasks
        long time = System.currentTimeMillis();
        while (!isCompleted.get()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (System.currentTimeMillis() - time > 1000) {
                System.out.println("Successful requests: " + totalSent.get());
                time = System.currentTimeMillis();
            }
        }

        // If any of the previous thread has finished tasks, create threads to send remaining requests
        System.out.println("Begin sending the remaining requests...");
        executor.setMaximumPoolSize(RESET_THREAD_NUM);
        executor.setCorePoolSize(RESET_THREAD_NUM);

        int prev = totalSent.get();
        while (!producer.isEmpty()) {
            if (executor.getActiveCount() == executor.getMaximumPoolSize()) {
                continue;
            }
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    int sent = 0;
                    for (int i = 0; i < TIMES; i++) {
                        SkiersApi apiInstance = new SkiersApi();
                        apiInstance.getApiClient().setBasePath(BASE_PATH);
                        Event event = producer.serve();
                        if (event == null) {
                            continue;
                        }
                        sent += client.sendRequest(apiInstance, event);
                    }
                    totalSent.addAndGet(sent);
                }
            });
            if (totalSent.get() - prev > 5000) {
                System.out.println("Current sent requests: " + totalSent.get());
                prev = totalSent.get();
            }
        }

        // Terminate executor
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        long end = System.currentTimeMillis();
        System.out.println("\n---------------------------------------------------------------------------------");
        System.out.println(totalSent.get() + " requests have been sent successfully for total.");
        System.out.println(SIZE - totalSent.get() + " requests have been sent unsuccessfully for total.");
        System.out.println("Time elapsed in milliseconds: " + (end - start));
        double throughput = totalSent.get()*1000.0/(end - start);
        System.out.println("Throughput: " + String.format("%.2f", throughput));

    }

    private int sendRequest(SkiersApi apiInstance, Event event) {
        for (int i = 0; i < RETRY; i++) {
            try {
                apiInstance.writeNewLiftRideWithHttpInfo(
                        event.getRide(),
                        event.getResortId(),
                        event.getSeasonId(),
                        event.getDayId(),
                        event.getSkierId());
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
