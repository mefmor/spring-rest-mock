package com.example.spring.boot;

import org.springframework.web.bind.annotation.*;

@RestController
public class StubController {
    private String responseBody = "Hello!";
    private String lastRequestBody;

    static final String ENDPOINT = "/endpoint";

    @GetMapping(ENDPOINT)
    public String getResponseForGet() {
        return responseBody;
    }

    @PostMapping(ENDPOINT)
    public String getResponseForPost(@RequestBody String requestBody) {
        this.lastRequestBody = requestBody;
        return responseBody;
    }

    @PutMapping(ENDPOINT + "/responseBody")
    public void setResponse(@RequestBody String responseBody) {
        this.responseBody = responseBody;
    }

    @GetMapping(ENDPOINT + "/lastRequestBody")
    public String getLastRequestBody() {
        return lastRequestBody;
    }

}
