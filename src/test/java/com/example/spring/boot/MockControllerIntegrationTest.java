package com.example.spring.boot;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockControllerIntegrationTest {
    @Autowired
    private TestRestTemplate endpoint;

    @Value("${default.response.path}")
    private String pathToDefaultResponseFile;

    @Test
    public void unauthorizedUserShouldNotHaveAccessToEndpoint() {
        assertThat(sendGetUnauthorizedRequest("/").getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void authorizedUserShouldHaveAccessToEndpoint() {
        assertThat(sendGetAuthorizedRequestToEndpoint().getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void byDefaultMockShouldUseResponseFromFile() {
        assertThat(sendGetAuthorizedRequestToEndpoint().getBody(), equalTo(Utils.readString(pathToDefaultResponseFile)));
    }

    @Test
    public void mockShouldReturnLastRequestBody() {
        sendPostAuthorizedRequestToEndpoint("Request message");

        assertThat(sendGetUnauthorizedRequest("/lastRequestBody").getBody(), equalTo("Request message"));
    }

    @Test
    public void userCanSetTheirOwnResponse() {
        String defaultResponse = sendGetAuthorizedRequestToEndpoint().getBody();
        try {
            sendPutAuthorizedRequest("/responseBody", "New response message");

            assertThat(sendGetAuthorizedRequestToEndpoint().getBody(), equalTo("New response message"));
        } finally {
            sendPutAuthorizedRequest("/responseBody", defaultResponse);
        }
    }

    private void sendPostAuthorizedRequestToEndpoint(String requestBody) {
        endpoint.withBasicAuth("user", "password")
                .postForEntity("/", requestBody, String.class);
    }

    private void sendPutAuthorizedRequest(String url, String requestBody) {
        endpoint.withBasicAuth("user", "password")
                .put(url, requestBody);
    }

    private ResponseEntity<String> sendGetAuthorizedRequestToEndpoint() {
        return endpoint.withBasicAuth("user", "password")
                .getForEntity("/", String.class);
    }

    private ResponseEntity<String> sendGetUnauthorizedRequest(String url) {
        return endpoint.getForEntity(url, String.class);
    }
}