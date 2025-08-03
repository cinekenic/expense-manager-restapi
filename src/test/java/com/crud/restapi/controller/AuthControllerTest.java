package com.crud.restapi.controller;

import com.crud.restapi.io.AuthRequest;
import com.crud.restapi.io.ProfileRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String uniqueEmail(String prefix) {
        return prefix + "_" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void register_shouldCreateNewProfile() throws Exception {
        String email = uniqueEmail("register");

        ProfileRequest request = new ProfileRequest();
        request.setName("Jan Kowalski");
        request.setEmail(email);
        request.setPassword("securePassword");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void login_shouldReturnJwtToken() throws Exception {
        String email = uniqueEmail("login");

        ProfileRequest reg = new ProfileRequest();
        reg.setName("Edyta Testowa");
        reg.setEmail(email);
        reg.setPassword("pass123");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        AuthRequest login = new AuthRequest(email, "pass123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void signout_shouldReturnNoContent() throws Exception {
        String email = uniqueEmail("signout");

        ProfileRequest reg = new ProfileRequest();
        reg.setName("Sign Out User");
        reg.setEmail(email);
        reg.setPassword("password");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        AuthRequest login = new AuthRequest(email, "password");

        String loginResponse = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwtToken = objectMapper.readTree(loginResponse).get("token").asText();

        mockMvc.perform(post("/signout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}
