package com.example.airlines.flyers;

import com.example.airlines.TestBase;
import com.example.airlines.Utils;
import com.example.airlines.model.Customer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AutoConfigureWebTestClient
public class FlyersHandlerTest extends TestBase {
    @Autowired
    private MockWebServer mockAirlinesService;
    @Autowired
    private WebTestClient webTestClient;
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();
    private static Logger LOG = LoggerFactory.getLogger(FlyersHandlerTest.class);

    @Test
    public void test_loadCustomerData(@Value("${airlines.application.endpoint}") final URL url) throws IOException, InterruptedException {
        mockAirlinesService .enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(customerAddedResponse));

        webTestClient.post()
                .uri("/airline/load/customers/5")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(customerAddedResponse);


        webTestClient.post()
                .uri("/airline/load/customers/-5")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo(badCustomerDataResponse);

        // Delete all data
        webTestClient.delete()
                .uri("/airline/customers/delete")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(deleteResponse);
    }

    @Test
    public void test_getFrequentFlyers(@Value("${airlines.application.endpoint}") final URL url) throws IOException, InterruptedException {
        mockAirlinesService .enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(customerAddedResponse));

        List<Customer> customerList = new ArrayList<>();
        List<Customer> frequentFlyers = getCustomers(10, true);
        List<Customer> notFrequentFlyers = getCustomers(10, false);
        // Get 10 valid customers
        customerList.addAll(frequentFlyers);
        // Get 10 invalid customers
        customerList.addAll(notFrequentFlyers);

        webTestClient.post()
                .uri("/airline/load/customers")
                .syncBody(GSON.toJson(customerList))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(customerAddedResponse);


        // Validate response is frequent flyers expected
        webTestClient.get()
                .uri("/airline/frequentflyers")
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .isEqualTo(GSON.toJson(frequentFlyers));

        // Delete all data
        webTestClient.delete()
                .uri("/airline/customers/delete")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(deleteResponse);

        // Validate empty data list while fetching on frequent flyers
        webTestClient.get()
                .uri("/airline/frequentflyers")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .isEqualTo(emptyDataListResponse);

        // Load not frequent flyers data
        webTestClient.post()
                .uri("/airline/load/customers")
                .syncBody(GSON.toJson(notFrequentFlyers))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(customerAddedResponse);

        // Validate response no frequent flyers
        webTestClient.get()
                .uri("/airline/frequentflyers")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .isEqualTo(emptyFrequentFlyersResponse);
    }


    public List<Customer> getCustomers(int noOfCustomers, boolean validCustomers) {
        List<Customer> customers = new ArrayList<>();
        int i = 0;
        int lowerLimit = 0;
        int upperLimit = 1000000;

        if(validCustomers) {
            lowerLimit = 100001;
        } else {
            upperLimit = 99999;
        }
        while(i++ < noOfCustomers) {
            Customer customer = new Customer(Utils.generateRandomName(), getRandomNumber(lowerLimit, upperLimit));
            customers.add(customer);
        }
        return customers;
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
