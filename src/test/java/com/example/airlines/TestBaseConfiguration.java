package com.example.airlines;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
public class TestBaseConfiguration {
    public static MockResponse FAIL_FAST_RESPONSE = new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);

    @Bean(destroyMethod = "shutdown")
    MockWebServer mockDPPService(@Value("${airlines.application.endpoint}") final URL url) throws IOException {
        final MockWebServer mockDPPService = new MockWebServer();
        okhttp3.mockwebserver.QueueDispatcher newDispatcher = new okhttp3.mockwebserver.QueueDispatcher();
        newDispatcher.setFailFast(FAIL_FAST_RESPONSE);
        mockDPPService.setDispatcher(newDispatcher);
        mockDPPService.start(url.getPort());
        return mockDPPService;
    }
}
