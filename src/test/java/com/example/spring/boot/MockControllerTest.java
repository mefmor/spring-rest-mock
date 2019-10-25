package com.example.spring.boot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest(SecurityConfig.class)
class MockControllerTest {
    @Autowired
    private MockMvc mock;

    @Test
    public void returnPositiveDefaultResponseByDefault() throws Exception {
        this.mock.perform(get("")).andExpect(status().isOk())
                .andExpect(content().string("Hello!"));
    }

    @Test
    public void canSetResponseForTheStub() throws Exception {
        this.mock.perform(put("").content("Goodbye!")).andExpect(status().isOk());

        this.mock.perform(get("")).andExpect(status().isOk())
                .andExpect(content().string("Hello!"));
    }
}