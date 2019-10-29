package com.example.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
public class MockController {
    private Logger logger = LoggerFactory.getLogger(MockController.class);

    private String responseBody;
    private String lastRequestBody;

    public MockController(@Value("${default.response.path}") String pathToDefaultResponseFile) {
        responseBody = Utils.readString(pathToDefaultResponseFile);
    }

    @GetMapping
    public String getResponseForGet(@RequestHeader HttpHeaders headers) {
        logger.info("GET request received from {} host", headers.getHost());
        return responseBody;
    }

    @PostMapping
    public String getResponseForPost(@RequestBody String requestBody, @RequestHeader HttpHeaders headers) {
        this.lastRequestBody = requestBody;
        logger.info("POST request received from {}", headers.getHost());
        logger.debug(requestBody);

        return responseBody;
    }

    @PutMapping("/responseBody")
    public void setResponse(@RequestBody String responseBody) {
        logger.info("New message body has been set");
        logger.debug(responseBody);

        this.responseBody = responseBody;
    }

    @GetMapping("/lastRequestBody")
    public String getLastRequestBody() {
        return lastRequestBody;
    }

}
