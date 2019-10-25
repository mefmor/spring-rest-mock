package com.example.spring.boot;

import org.springframework.web.bind.annotation.*;

@RestController
public class MockController {
    private String responseBody = "Hello!";
    private String lastRequestBody;

    @GetMapping
    public String getResponseForGet() {
        return responseBody;
    }

    @PostMapping
    public String getResponseForPost(@RequestBody String requestBody) {
        this.lastRequestBody = requestBody;
        return responseBody;
    }

    @PutMapping("/responseBody")
    public void setResponse(@RequestBody String responseBody) {
        this.responseBody = responseBody;
    }

    @GetMapping("/lastRequestBody")
    public String getLastRequestBody() {
        return lastRequestBody;
    }

}
