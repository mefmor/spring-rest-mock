package com.example.spring.boot;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

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
        endpoint.withBasicAuth("user", "password")
                .postForEntity("/", "Request message", String.class);

        assertThat(sendGetUnauthorizedRequest("/lastRequestBody").getBody(), equalTo("Request message"));
    }

    @Test
    public void userCanSetTheirOwnResponse() {
        String defaultResponse = sendGetAuthorizedRequestToEndpoint().getBody();
        try {
            assertThat(sendPostUnauthorizedRequestToEndpoint("/responseBody", "New response message").getStatusCode(),
                    equalTo(HttpStatus.OK));
            assertThat(sendGetAuthorizedRequestToEndpoint().getBody(), equalTo("New response message"));
        } finally {
            sendPostUnauthorizedRequestToEndpoint("/responseBody", defaultResponse);
        }
    }

    @Test
    public void userCanSetTheirOwnResponseCode() {
        Integer defaultResponseCode = sendGetAuthorizedRequestToEndpoint().getStatusCodeValue();
        try {
            assertThat(sendPostUnauthorizedRequestToEndpoint("/responseCode", "410").getStatusCode(),
                    equalTo(HttpStatus.OK));
            assertThat(sendGetAuthorizedRequestToEndpoint().getStatusCode(), equalTo(HttpStatus.GONE));
        } finally {
            sendPostUnauthorizedRequestToEndpoint("/responseCode", defaultResponseCode);
        }
    }

    @Test
    public void inCaseOfNotExistingResponseCodeUserGetMessageAndResponseRemainUnchanged() {
        ResponseEntity<String> response = sendPostUnauthorizedRequestToEndpoint("/responseCode", "666");

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), equalTo("[666] is wrong HTTP status code"));
        assertThat(sendGetAuthorizedRequestToEndpoint().getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void inCaseOfIncorrectResponseCodeUserGetMessageAndResponseRemainUnchanged() {
        ResponseEntity<String> response = sendPostUnauthorizedRequestToEndpoint("/responseCode", "OMG!");

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), equalTo("[OMG!] is wrong HTTP status code"));
        assertThat(sendGetAuthorizedRequestToEndpoint().getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void userCanSetTheirOwnHeader() {
        sendPostUnauthorizedRequestToEndpoint("/responseHeaders/Accept", "text/xml, application/soap+xml");
        sendPostUnauthorizedRequestToEndpoint("/responseHeaders/content-type", "text/xml; charset=\"UTF-8\"");
        sendPostUnauthorizedRequestToEndpoint("/responseHeaders/Server", "IOS Servlet Container");
        HttpHeaders responseHeaders = sendGetAuthorizedRequestToEndpoint().getHeaders();

        assertThat(responseHeaders.getAccept().toString(), equalTo("[text/xml, application/soap+xml]"));
        assertThat(Objects.requireNonNull(responseHeaders.getContentType()).toString(), equalTo("text/xml;charset=UTF-8"));
        assertThat(Objects.requireNonNull(responseHeaders.get("Server")).toString(), equalTo("[IOS Servlet Container]"));
    }

    private ResponseEntity<String> sendPostUnauthorizedRequestToEndpoint(String url, Object requestBody) {
        return endpoint.withBasicAuth("user", "password")
                .postForEntity(url, requestBody, String.class);
    }

    private ResponseEntity<String> sendGetAuthorizedRequestToEndpoint() {
        return endpoint.withBasicAuth("user", "password")
                .getForEntity("/", String.class);
    }

    private ResponseEntity<String> sendGetUnauthorizedRequest(String url) {
        return endpoint.getForEntity(url, String.class);
    }
}