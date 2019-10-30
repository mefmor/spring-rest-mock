package com.example.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MockController {
    private Logger logger = LoggerFactory.getLogger(MockController.class);

    private String responseBody;
    private HttpStatus httpStatus;
    private HttpHeaders responseHeaders = new HttpHeaders();
    private String lastRequestBody;

    public MockController(@Value("${default.response.path}") String pathToDefaultResponseFile,
                          @Value("${default.response.status.code}") int httpResponseCode) {
        responseBody = Utils.readString(pathToDefaultResponseFile);
        httpStatus = HttpStatus.resolve(httpResponseCode);
    }

    @GetMapping
    public ResponseEntity<String> getResponseForGet(@RequestHeader HttpHeaders headers) {
        logger.info("GET request received from {} host", headers.getHost());

        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(responseBody);
    }

    @PostMapping
    public ResponseEntity<String> getResponseForPost(@RequestBody String requestBody, @RequestHeader HttpHeaders headers) {
        this.lastRequestBody = requestBody;
        logger.info("POST request received from {}", headers.getHost());
        logger.debug(requestBody);

        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(responseBody);
    }

    @PostMapping("/responseBody")
    public void setResponseBody(@RequestBody String responseBody) {
        logger.info("New message body has been set");
        logger.debug(responseBody);

        this.responseBody = responseBody;
    }

    @PostMapping("/responseCode")
    public ResponseEntity<String> setResponseCode(@RequestBody String httpResponseCode) {
        try {
            HttpStatus newHttpStatus = HttpStatus.resolve(Integer.parseInt(httpResponseCode));
            if (newHttpStatus == null)
                throw new IllegalArgumentException();

            httpStatus = newHttpStatus;
            logger.info("New response code {} has been set", httpResponseCode);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            logger.error("Trying to set wrong response code [{}]", httpResponseCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("[%s] is wrong HTTP status code", httpResponseCode));
        }
    }

    @PostMapping("/responseHeaders/{header}")
    public void setResponseHeader(@PathVariable("header") String header, @RequestBody String value) {
        responseHeaders.set(header, value);
        logger.debug("New HTTP header with name [{}] and value [{}] added", header, value);
    }

    @GetMapping("/lastRequestBody")
    public String getLastRequestBody() {
        return lastRequestBody;
    }
}
