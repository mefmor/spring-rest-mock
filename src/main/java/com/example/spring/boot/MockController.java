package com.example.spring.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
public class MockController {
    private Logger logger = LoggerFactory.getLogger(MockController.class);

    private String responseBody;
    private String lastRequestBody;

    public MockController(@Value("${default.response.path}") String pathToDefaultResponseFile) {
        Resource r = new DefaultResourceLoader().getResource(pathToDefaultResponseFile);
        responseBody = asString(r);
    }

    private static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
