package com.bisa.sis.auth;

import com.bisa.sis.PostgresTestContainerConfig;
import com.bisa.sis.user.Role;
import com.bisa.sis.user.User;
import com.bisa.sis.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.jwt.secret=test-only-secret-key-at-least-32-bytes-long-xx",
        "app.cookie.secure=false",
        "app.bootstrap.super-admin-email=",
        "app.bootstrap.super-admin-password="
})
@AutoConfigureMockMvc
@Import(PostgresTestContainerConfig.class)
class AuthFlowIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(new User(
                "lecturer@bisa.test", passwordEncoder.encode("Sup3rSecret!"), "Lee", "Turner", Role.LECTURER));
    }

    @Test
    void loginWithValidCredentialsReturnsAccessTokenAndSetsRefreshCookie() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "lecturer@bisa.test", "password", "Sup3rSecret!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.role").value("LECTURER"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token", true));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "lecturer@bisa.test", "password", "wrong-password"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshRotatesTokenAndOldCookieCanNoLongerBeUsed() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "lecturer@bisa.test", "password", "Sup3rSecret!"))))
                .andReturn();
        Cookie originalRefreshCookie = loginResult.getResponse().getCookie("refresh_token");
        assertThat(originalRefreshCookie).isNotNull();

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh").cookie(originalRefreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();
        Cookie rotatedCookie = refreshResult.getResponse().getCookie("refresh_token");
        assertThat(rotatedCookie.getValue()).isNotEqualTo(originalRefreshCookie.getValue());

        // Reusing the original (now-revoked) refresh token must fail.
        mockMvc.perform(post("/api/auth/refresh").cookie(originalRefreshCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevokesTheRefreshToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "lecturer@bisa.test", "password", "Sup3rSecret!"))))
                .andReturn();
        Cookie refreshCookie = loginResult.getResponse().getCookie("refresh_token");

        mockMvc.perform(post("/api/auth/logout").cookie(refreshCookie))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/refresh").cookie(refreshCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointRejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent()); // logout tolerates no cookie

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
