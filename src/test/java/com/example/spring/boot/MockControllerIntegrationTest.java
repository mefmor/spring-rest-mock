package com.example.spring.boot;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MockControllerIntegrationTest {
    @Autowired
    private TestRestTemplate template;

    @Test
    public void unauthorizedUserShouldNotHaveAccessToEndpoint() {
        ResponseEntity<String> response = template.getForEntity("/", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void authorizedUserShouldHaveAccessToEndpoint() {
        ResponseEntity<String> response = template.withBasicAuth("user", "password")
                .getForEntity("/", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }
}