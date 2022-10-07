package io.swagger.api;

import io.swagger.model.LiftRide;
import io.swagger.model.ResponseMsg;
import io.swagger.model.SkierVertical;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-10-04T01:01:20.897Z[GMT]")
@RestController
public class SkiersApiController implements SkiersApi {

    private final int THIS_YEAR = 2022;
    private final int BEGIN_YEAR = 2000;
    private final int MIN_DAY = 1;
    private final int MAX_DAY = 31;

    private static final Logger log = LoggerFactory.getLogger(SkiersApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public SkiersApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Integer> getSkierDayVertical(@Parameter(in = ParameterIn.PATH, description = "ID of the resort the skier is at", required=true, schema=@Schema()) @PathVariable("resortID") Integer resortID,@Parameter(in = ParameterIn.PATH, description = "ID of the ski season", required=true, schema=@Schema()) @PathVariable("seasonID") String seasonID,@DecimalMin("1") @DecimalMax("366") @Parameter(in = ParameterIn.PATH, description = "ID number of ski day in the ski season", required=true, schema=@Schema()) @PathVariable("dayID") String dayID,@Parameter(in = ParameterIn.PATH, description = "ID of the skier riding the lift", required=true, schema=@Schema()) @PathVariable("skierID") Integer skierID) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Integer>(objectMapper.readValue("34507", Integer.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Integer>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Integer>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<SkierVertical> getSkierResortTotals(@Parameter(in = ParameterIn.PATH, description = "ID the skier to retrieve data for", required=true, schema=@Schema()) @PathVariable("skierID") Integer skierID,@NotNull @Parameter(in = ParameterIn.QUERY, description = "resort to filter by" ,required=true,schema=@Schema()) @Valid @RequestParam(value = "resort", required = true) List<String> resort,@Parameter(in = ParameterIn.QUERY, description = "season to filter by, optional" ,schema=@Schema()) @Valid @RequestParam(value = "season", required = false) List<String> season) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<SkierVertical>(objectMapper.readValue("{\n  \"resorts\" : [ {\n    \"seasonID\" : \"seasonID\",\n    \"totalVert\" : 0\n  }, {\n    \"seasonID\" : \"seasonID\",\n    \"totalVert\" : 0\n  } ]\n}", SkierVertical.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<SkierVertical>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<SkierVertical>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> writeNewLiftRide(@Parameter(in = ParameterIn.PATH, description = "ID of the resort the skier is at", required=true, schema=@Schema()) @PathVariable("resortID") Integer resortID,@Parameter(in = ParameterIn.PATH, description = "ID of the ski season", required=true, schema=@Schema()) @PathVariable("seasonID") String seasonID,@DecimalMin("1") @DecimalMax("366") @Parameter(in = ParameterIn.PATH, description = "ID number of ski day in the ski season", required=true, schema=@Schema()) @PathVariable("dayID") String dayID,@Parameter(in = ParameterIn.PATH, description = "ID of the skier riding the lift", required=true, schema=@Schema()) @PathVariable("skierID") Integer skierID,@Parameter(in = ParameterIn.DEFAULT, description = "Specify new Season value", required=true, schema=@Schema()) @Valid @RequestBody LiftRide body) {
        String accept = request.getHeader("Accept");
        if (Integer.valueOf(seasonID) > THIS_YEAR || Integer.valueOf(seasonID) < BEGIN_YEAR || Integer.valueOf(dayID) > MAX_DAY || Integer.valueOf(dayID) < MIN_DAY) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

}
