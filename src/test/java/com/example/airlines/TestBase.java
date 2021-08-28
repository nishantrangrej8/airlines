package com.example.airlines;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWebTestClient
public class TestBase {
    public static String deleteResponse = "All customer data deleted";
    public static String emptyDataListResponse = "{\"errorMessage\":\"Customer data list is empty. Please run /flyers/load/{noOfCustomers}\",\"code\":4040}";
    public static String emptyFrequentFlyersResponse ="{\"errorMessage\":\"No Frequent flyers found.\",\"code\":4040}";
    public static String customerAddedResponse = "{\"status\": \"OK\"}";
    public static String badCustomerDataResponse = "No. of customers invalid. Please provide valid value between 0 & 100000 value";
}
