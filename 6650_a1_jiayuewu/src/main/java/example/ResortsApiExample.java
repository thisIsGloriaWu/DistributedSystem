package example;

import io.swagger.client.*;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.ResortsApi;

import java.io.File;
import java.util.*;

public class ResortsApiExample {

    public static void main(String[] args) {

        SkiersApi apiInstance = new SkiersApi();
        apiInstance.getApiClient().setBasePath("http://35.91.140.147:8080/swagger-spring");
        Integer resortID = 56; // Integer | ID of the resort of interest
        Integer seasonID = 56; // Integer | ID of the resort of interest
        Integer dayID = 56; // Integer | ID of the resort of interest
        try {
            ApiResponse<Void> response = apiInstance.writeNewLiftRideWithHttpInfo(new LiftRide(), 1, "2022", "1", 1);
            System.out.println(response);
        } catch (ApiException e) {
            System.err.println("Exception when calling ResortsApi#getResortSkiersDay");
            e.printStackTrace();
        }
    }
}