package ru.bookingsystem.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private MockMvc mockMvc;

    @Test
    void addUser() throws Exception {

        String jsonRequest = "{" +
                "\"username\":\"testName\"," +
                "\"password\":\"testPassword\"," +
                "\"confirmPassword\":\"testPassword\"," +
                "\"email\":\"testEmail@gmail.com\"" +
                "}";

        mockMvc.perform(
                        post("/api/users/addUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testName"))
                .andExpect(jsonPath("$.email").value("testEmail@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.activationCode").isNotEmpty());
    }
}