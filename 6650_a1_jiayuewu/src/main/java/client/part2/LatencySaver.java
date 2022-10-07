package client.part2;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LatencySaver implements Runnable {

    private static final SimpleDateFormat formator = new SimpleDateFormat("hh:mm:ss");
    private final LinkedBlockingQueue<String[]> latencyQueue = new LinkedBlockingQueue<>();
    private final String FILE_PATH = "target/output";
    private int size;
    private final List<Long> responseTime = new ArrayList<>();

    public LatencySaver(int size) {
        this.size = size;
    }

    public void receive(long start, long latency, int code) {
        String[] info = new String[4];
        info[0] = formator.format(new Date(start));
        info[1] = "POST";
        info[2] = Long.toString(latency);
        responseTime.add(latency);
        info[3] = Integer.toString(code);
        latencyQueue.add(info);
    }

    @Override
    public void run() {
        try {
            FileWriter outputFile = new FileWriter(FILE_PATH, false);
            CSVWriter writer = new CSVWriter(outputFile);
            String[] header = {"Start Time", "Request Type", "Latency", "Response Code"};
            writer.writeNext(header, false);
            int count = size;
            while (count > 0) {
                String[] record = latencyQueue.poll();
                if (record != null) {
                    writer.writeNext(record, false);
                    count--;
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        // calculate response time information
        int timeSize = responseTime.size();
        Long[] times = responseTime.toArray(responseTime.toArray(new Long[0]));
        Arrays.sort(times);

        // calculate mean response time
        long sum = 0;
        for (Long time: times) {
            sum += time;
        }
        double mean = sum * 1.0 / timeSize;

        // calculate medium response time
        double medium = 0;
        if (timeSize % 2 == 0) {
            medium = (double)(times[(timeSize - 1) / 2] + times[timeSize / 2]) / 2.0;
        } else {
            medium = (double)times[timeSize / 2];
        }

        // calculate 99% response time
        long percentage = times[(int)(timeSize * 0.99)];

        // calculate max and min response time
        long max = times[timeSize-1];
        long min = times[0];

        System.out.println("Mean Response Time: " + String.format("%.2f", mean));
        System.out.println("Medium Response Time: " + String.format("%.2f", medium));
        System.out.println("P99 Response Time: " + percentage);
        System.out.println("Max Response Time: " + max);
        System.out.println("Min Response Time: " + min);
    }
}
