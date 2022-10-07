package common;

import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private static final DataGenerator INSTANCE = new DataGenerator();

    private final int MIN = 1;
    private final String SEASON = "2022";

    private final String DAY = "1";
    private final int SKIER_MAX = 100000;
    private final int RESORT_MAX = 10;
    private final int LIFT_MAX = 40;
    private final int TIME_MAX = 360;

    public static DataGenerator getInstance() {
        return INSTANCE;

    }
    protected DataGenerator() {
    }

    public int getRandomSkierId() {
        return ThreadLocalRandom.current().nextInt(MIN, SKIER_MAX+1);
    }

    public int getRandomResortId() {
        return ThreadLocalRandom.current().nextInt(MIN, RESORT_MAX+1);
    }

    public int getRandomLiftId() {
        return ThreadLocalRandom.current().nextInt(MIN, LIFT_MAX+1);
    }

    public String getRandomSeasonId() {
        return SEASON;
    }

    public String getRandomDayId() {
        return DAY;
    }

    public int getRandomTime() {
        return ThreadLocalRandom.current().nextInt(MIN, TIME_MAX+1);
    }

}
