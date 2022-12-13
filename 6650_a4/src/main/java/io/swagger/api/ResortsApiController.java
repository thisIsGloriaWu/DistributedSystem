package io.swagger.api;

import io.swagger.model.ResortIDSeasonsBody;
import io.swagger.model.ResortSkiers;
import io.swagger.model.ResortsList;
import io.swagger.model.ResponseMsg;
import io.swagger.model.SeasonsList;
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
import redis.RedisTemplate;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-10-04T01:01:20.897Z[GMT]")
@RestController
public class ResortsApiController implements ResortsApi {

    private static final Logger log = LoggerFactory.getLogger(ResortsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final RedisTemplate redis = new RedisTemplate();

    private final int THIS_YEAR = 2022;
    private final int BEGIN_YEAR = 2000;
    private final int MIN_DAY = 1;
    private final int MAX_DAY = 31;

    @org.springframework.beans.factory.annotation.Autowired
    public ResortsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> addSeason(@Parameter(in = ParameterIn.PATH, description = "ID of the resort of interest", required=true, schema=@Schema()) @PathVariable("resortID") Integer resortID,@Parameter(in = ParameterIn.DEFAULT, description = "Specify new Season value", required=true, schema=@Schema()) @Valid @RequestBody ResortIDSeasonsBody body) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<SeasonsList> getResortSeasons(@Parameter(in = ParameterIn.PATH, description = "ID of the resort of interest", required=true, schema=@Schema()) @PathVariable("resortID") Integer resortID) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<SeasonsList>(objectMapper.readValue("{\n  \"seasons\" : [ \"seasons\", \"seasons\" ]\n}", SeasonsList.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<SeasonsList>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<SeasonsList>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ResortSkiers> getResortSkiersDay(@Parameter(in = ParameterIn.PATH, description = "ID of the resort of interest", required=true, schema=@Schema()) @PathVariable("resortID") Integer resortID,@Parameter(in = ParameterIn.PATH, description = "ID of the resort of interest", required=true, schema=@Schema()) @PathVariable("seasonID") Integer seasonID,@Parameter(in = ParameterIn.PATH, description = "ID of the resort of interest", required=true, schema=@Schema()) @PathVariable("dayID") Integer dayID) {
        // check invalid input
        if (seasonID < BEGIN_YEAR || seasonID > THIS_YEAR || dayID < MIN_DAY || dayID > MAX_DAY) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Integer num = redis.getUniqueSkiersCount(resortID.toString(), seasonID.toString(), dayID.toString());
        ResortSkiers resortSkiers = new ResortSkiers();
        resortSkiers.setTime(String.join("/", seasonID.toString(), dayID.toString()));
        resortSkiers.setNumSkiers(num);
        return new ResponseEntity<ResortSkiers>(resortSkiers, HttpStatus.OK);
    }

    public ResponseEntity<ResortsList> getResorts() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ResortsList>(objectMapper.readValue("{\n  \"resorts\" : [ {\n    \"resortName\" : \"resortName\",\n    \"resortID\" : 0\n  }, {\n    \"resortName\" : \"resortName\",\n    \"resortID\" : 0\n  } ]\n}", ResortsList.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ResortsList>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ResortsList>(HttpStatus.NOT_IMPLEMENTED);
    }

}
