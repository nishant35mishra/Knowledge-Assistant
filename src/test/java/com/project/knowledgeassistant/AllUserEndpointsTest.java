package com.project.knowledgeassistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.knowledgeassistant.DTOs.AuthResponseDto;
import com.project.knowledgeassistant.DTOs.LoginDto;
import com.project.knowledgeassistant.DTOs.RefreshTokenRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
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
public class AllUserEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Comprehensive Test for All User Endpoints")
    public void testAllUserEndpoints() throws Exception {
        // ==========================================
        // 1. Admin Login (Seed from DataInitializer)
        // ==========================================
        LoginDto adminLogin = new LoginDto();
        adminLogin.setEmail("admin@algoworks.com");
        adminLogin.setPassword("Admin@123");

        MvcResult adminLoginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDto adminAuth = objectMapper.readValue(adminLoginResult.getResponse().getContentAsString(), AuthResponseDto.class);
        assertNotNull(adminAuth.getAccessToken(), "Admin access token should be present");
        assertNotNull(adminAuth.getRefreshToken(), "Admin refresh token should be present");
        String adminToken = adminAuth.getAccessToken();

        // ==========================================
        // 2. POST /user/add - Validation Tests (Regex)
        // ==========================================
        // 2a. Invalid Email (not @algoworks.com) -> 400 Bad Request
        RegisterRequestDto badEmailUser = new RegisterRequestDto();
        badEmailUser.setUsername("baduser");
        badEmailUser.setEmail("baduser@gmail.com");
        badEmailUser.setPassword("Valid@123");
        badEmailUser.setRole("EMPLOYEE");

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badEmailUser)))
                .andExpect(status().isBadRequest());

        // 2b. Invalid Password (< 8 chars / no special char) -> 400 Bad Request
        RegisterRequestDto badPasswordUser = new RegisterRequestDto();
        badPasswordUser.setUsername("badpass");
        badPasswordUser.setEmail("badpass@algoworks.com");
        badPasswordUser.setPassword("weak");
        badPasswordUser.setRole("EMPLOYEE");

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPasswordUser)))
                .andExpect(status().isBadRequest());

        // 2c. Valid User Registration -> 201 Created
        String testEmpEmail = "emp_test_" + System.currentTimeMillis() + "@algoworks.com";
        RegisterRequestDto validUser = new RegisterRequestDto();
        validUser.setUsername("validemp");
        validUser.setEmail(testEmpEmail);
        validUser.setPassword("Valid@123");
        validUser.setRole("EMPLOYEE");

        MvcResult registerResult = mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andReturn();

        RegisterResponseDto regResponse = objectMapper.readValue(registerResult.getResponse().getContentAsString(), RegisterResponseDto.class);
        int empId = regResponse.getId();
        assertTrue(empId > 0, "Registered user should have a valid ID");

        // 2d. Duplicate Registration -> 400 Bad Request
        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());

        // ==========================================
        // 3. POST /user/login Before Approval (Should Fail)
        // ==========================================
        LoginDto pendingLogin = new LoginDto();
        pendingLogin.setEmail(testEmpEmail);
        pendingLogin.setPassword("Valid@123");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pendingLogin)))
                .andExpect(status().is5xxServerError()); // Current RuntimeException maps to 500

        // ==========================================
        // 4. GET /user/pending (Admin should see pending users)
        // ==========================================
        MvcResult pendingListResult = mockMvc.perform(get("/user/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(pendingListResult.getResponse().getContentAsString().contains(testEmpEmail),
                "Pending list must include the newly registered employee");

        // ==========================================
        // 5. POST /user/approve/{id}
        // ==========================================
        // 5a. Admin approves pending employee -> 200 OK
        MvcResult approveResult = mockMvc.perform(post("/user/approve/" + empId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(approveResult.getResponse().getContentAsString().contains("approved successfully"));

        // 5b. Re-approval of already approved user -> Should Fail
        mockMvc.perform(post("/user/approve/" + empId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is5xxServerError());

        // ==========================================
        // 6. POST /user/login After Approval (Should Succeed)
        // ==========================================
        MvcResult empLoginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pendingLogin)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDto empAuth = objectMapper.readValue(empLoginResult.getResponse().getContentAsString(), AuthResponseDto.class);
        assertNotNull(empAuth.getAccessToken(), "Employee access token should be present");
        assertNotNull(empAuth.getRefreshToken(), "Employee refresh token should be present");
        String empAccessToken = empAuth.getAccessToken();
        String empRefreshToken = empAuth.getRefreshToken();

        // ==========================================
        // 7. POST /user/refresh
        // ==========================================
        RefreshTokenRequestDto refreshDto = new RefreshTokenRequestDto();
        refreshDto.setRefreshToken(empRefreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDto refreshedAuth = objectMapper.readValue(refreshResult.getResponse().getContentAsString(), AuthResponseDto.class);
        assertNotNull(refreshedAuth.getAccessToken(), "Refreshed access token must not be null");

        // ==========================================
        // 8. GET /user (All Users) - Role-Based Access Control
        // ==========================================
        // 8a. Admin can access GET /user -> 200 OK
        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 8b. Employee cannot access GET /user -> 403 Forbidden
        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + empAccessToken))
                .andExpect(status().isForbidden());

        // ==========================================
        // 9. Rejection Flow (POST /user/reject/{id})
        // ==========================================
        // 9a. Register a second employee to test rejection
        String rejectEmpEmail = "emp_reject_" + System.currentTimeMillis() + "@algoworks.com";
        RegisterRequestDto rejectCandidate = new RegisterRequestDto();
        rejectCandidate.setUsername("rejectemp");
        rejectCandidate.setEmail(rejectEmpEmail);
        rejectCandidate.setPassword("Valid@123");
        rejectCandidate.setRole("EMPLOYEE");

        MvcResult regRejectResult = mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectCandidate)))
                .andExpect(status().isCreated())
                .andReturn();

        int rejectEmpId = objectMapper.readValue(regRejectResult.getResponse().getContentAsString(), RegisterResponseDto.class).getId();

        // 9b. Admin rejects user -> 200 OK
        MvcResult rejectResult = mockMvc.perform(post("/user/reject/" + rejectEmpId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(rejectResult.getResponse().getContentAsString().contains("rejected"));

        // 9c. Re-rejecting already rejected user -> Should Fail
        mockMvc.perform(post("/user/reject/" + rejectEmpId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is5xxServerError());

        // 9d. Approving a rejected user -> Should Fail
        mockMvc.perform(post("/user/approve/" + rejectEmpId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is5xxServerError());

        // 9e. Rejecting an active user -> Should Fail
        mockMvc.perform(post("/user/reject/" + empId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is5xxServerError());

        // ==========================================
        // 10. POST /user/logout & Token Blacklisting Test
        // ==========================================
        // 10a. Verify employee token works on protected /test3 (EMPLOYEE allowed)
        mockMvc.perform(get("/test3")
                        .header("Authorization", "Bearer " + empAccessToken))
                .andExpect(status().isOk());

        // 10b. Logout with accessToken and refreshToken
        RefreshTokenRequestDto logoutDto = new RefreshTokenRequestDto();
        logoutDto.setRefreshToken(empRefreshToken);

        mockMvc.perform(post("/user/logout")
                        .header("Authorization", "Bearer " + empAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutDto)))
                .andExpect(status().isOk());

        // 10c. CRITICAL: Attempt to access protected endpoint with the logged-out token -> 401 Unauthorized
        mockMvc.perform(get("/test3")
                        .header("Authorization", "Bearer " + empAccessToken))
                .andExpect(status().isUnauthorized());

        // 10d. CRITICAL: Attempt to refresh token after logout -> Should Fail
        mockMvc.perform(post("/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutDto)))
                .andExpect(status().is5xxServerError());
    }
}
