package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.LiftRide;
import io.swagger.model.SkierVertical;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rabbitmq.RemoteQueue;
import redis.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
public class SkiersApiController implements SkiersApi {

    private static final Logger log = LoggerFactory.getLogger(SkiersApiController.class);
    private final int THIS_YEAR = 2022;
    private final int BEGIN_YEAR = 2000;
    private final int MIN_DAY = 1;
    private final int MAX_DAY = 31;

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    private final RemoteQueue queue = new RemoteQueue(16);

    private final RedisTemplate redis = new RedisTemplate();

    @org.springframework.beans.factory.annotation.Autowired
    public SkiersApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Integer> getSkierDayVertical(@Parameter(in = ParameterIn.PATH, description = "ID of the resort the skier is at", required = true, schema = @Schema()) @PathVariable("resortID") Integer resortID, @Parameter(in = ParameterIn.PATH, description = "ID of the ski season", required = true, schema = @Schema()) @PathVariable("seasonID") String seasonID, @DecimalMin("1") @DecimalMax("366") @Parameter(in = ParameterIn.PATH, description = "ID number of ski day in the ski season", required = true, schema = @Schema()) @PathVariable("dayID") String dayID, @Parameter(in = ParameterIn.PATH, description = "ID of the skier riding the lift", required = true, schema = @Schema()) @PathVariable("skierID") Integer skierID) {
        // check invalid input
        if (Integer.parseInt(seasonID) < BEGIN_YEAR || Integer.parseInt(seasonID) > THIS_YEAR || Integer.parseInt(dayID) < MIN_DAY || Integer.parseInt(dayID) > MAX_DAY) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Integer vertical = redis.getVerticalForSkier(skierID.toString(), resortID.toString(), seasonID, dayID);
        return new ResponseEntity<>(vertical * 10, HttpStatus.OK);
    }

    public ResponseEntity<SkierVertical> getSkierResortTotals(@Parameter(in = ParameterIn.PATH, description = "ID the skier to retrieve data for", required = true, schema = @Schema()) @PathVariable("skierID") Integer skierID, @NotNull @Parameter(in = ParameterIn.QUERY, description = "resort to filter by", required = true, schema = @Schema()) @Valid @RequestParam(value = "resort", required = true) List<String> resort, @Parameter(in = ParameterIn.QUERY, description = "season to filter by, optional", schema = @Schema()) @Valid @RequestParam(value = "season", required = false) List<String> season) {
        HashSet<String> resorts = new HashSet<>(resort);
        HashSet<String> seasons = new HashSet<>();
        if (season != null) {
            // check invalid input
            for (String item: season) {
                if (Integer.parseInt(item) < BEGIN_YEAR || Integer.parseInt(item) > THIS_YEAR) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                seasons.add(item);
            }
        }

        SkierVertical verticals = redis.getVerticalsPerSeason(skierID.toString(), resorts, seasons);
        return new ResponseEntity<>(verticals, HttpStatus.OK);
    }

    public ResponseEntity<Void> writeNewLiftRide(@Parameter(in = ParameterIn.PATH, description = "ID of the resort the skier is at", required = true, schema = @Schema()) @PathVariable("resortID") Integer resortID, @Parameter(in = ParameterIn.PATH, description = "ID of the ski season", required = true, schema = @Schema()) @PathVariable("seasonID") String seasonID, @DecimalMin("1") @DecimalMax("366") @Parameter(in = ParameterIn.PATH, description = "ID number of ski day in the ski season", required = true, schema = @Schema()) @PathVariable("dayID") String dayID, @Parameter(in = ParameterIn.PATH, description = "ID of the skier riding the lift", required = true, schema = @Schema()) @PathVariable("skierID") Integer skierID, @Parameter(in = ParameterIn.DEFAULT, description = "Specify new Season value", required = true, schema = @Schema()) @Valid @RequestBody LiftRide body) {
        if (body == null || Integer.valueOf(seasonID) > THIS_YEAR || Integer.valueOf(seasonID) < BEGIN_YEAR || Integer.valueOf(dayID) > MAX_DAY || Integer.valueOf(dayID) < MIN_DAY) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        queue.postMessage(skierID, resortID, seasonID, dayID, body.getLiftID());
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
