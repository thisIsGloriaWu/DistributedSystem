package client.part1;

import io.swagger.client.model.LiftRide;

public class Event {
    private LiftRide ride;
    private int resortId;
    private String seasonId;
    private String dayId;
    private int skierId;

    public Event(LiftRide ride, int resortId, String seasonId, String dayId, int skierId) {
        this.ride = ride;
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.skierId = skierId;
    }

    public LiftRide getRide() {
        return ride;
    }

    public int getResortId() {
        return resortId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public String getDayId() {
        return dayId;
    }

    public int getSkierId() {
        return skierId;
    }
}
