package com.project.knowledgeassistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.knowledgeassistant.DTOs.AuthResponseDto;
import com.project.knowledgeassistant.DTOs.LoginDto;
import com.project.knowledgeassistant.DTOs.RefreshTokenRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthAndTokenFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullAuthenticationAndRefreshTokenFlow() throws Exception {
        // 1. Login with admin credentials
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("admin@algoworks.com");
        loginDto.setPassword("Admin@123");

        MvcResult loginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponseBody = loginResult.getResponse().getContentAsString();
        AuthResponseDto authResponse = objectMapper.readValue(loginResponseBody, AuthResponseDto.class);

        assertNotNull(authResponse.getAccessToken(), "Access token must not be null");
        assertNotNull(authResponse.getRefreshToken(), "Refresh token must not be null");
        assertTrue(authResponse.getRoles().contains("ROLE_ADMIN"), "Roles must include ROLE_ADMIN");

        String accessToken = authResponse.getAccessToken();
        String refreshToken = authResponse.getRefreshToken();

        // 2. Call protected /test1 with valid access token -> Should return 200 OK with "success"
        MvcResult test1Result = mockMvc.perform(get("/test1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("success", test1Result.getResponse().getContentAsString());

        // 3. Call protected /test1 WITHOUT token -> Should return 403 Forbidden
        mockMvc.perform(get("/test1"))
                .andExpect(status().isForbidden());

        // 4. Use the refresh token to get a new access token
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
        refreshRequest.setRefreshToken(refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshResponseBody = refreshResult.getResponse().getContentAsString();
        AuthResponseDto newAuthResponse = objectMapper.readValue(refreshResponseBody, AuthResponseDto.class);

        assertNotNull(newAuthResponse.getAccessToken(), "New access token must not be null");
        String newAccessToken = newAuthResponse.getAccessToken();

        // 5. Call protected /test1 with the NEW access token -> Should return 200 OK with "success"
        MvcResult newTest1Result = mockMvc.perform(get("/test1")
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("success", newTest1Result.getResponse().getContentAsString());
    }
}
