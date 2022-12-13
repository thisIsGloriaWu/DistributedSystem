package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Objects;

/**
 * LiftRide
 */

public class LiftRide implements Serializable {
    @JsonProperty("time")
    private Integer time = null;

    @JsonProperty("liftID")
    private Integer liftID = null;

    public LiftRide time(Integer time) {
        this.time = time;
        return this;
    }

    /**
     * Get time
     *
     * @return time
     **/
    @Schema(example = "217", description = "")

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public LiftRide liftID(Integer liftID) {
        this.liftID = liftID;
        return this;
    }

    /**
     * Get liftID
     *
     * @return liftID
     **/
    @Schema(example = "21", description = "")

    public Integer getLiftID() {
        return liftID;
    }

    public void setLiftID(Integer liftID) {
        this.liftID = liftID;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiftRide liftRide = (LiftRide) o;
        return Objects.equals(this.time, liftRide.time) && Objects.equals(this.liftID, liftRide.liftID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, liftID);
    }

    @Override
    public String toString() {
        return "LiftRide{" + "time=" + time + ", liftID=" + liftID + '}';
    }
}
