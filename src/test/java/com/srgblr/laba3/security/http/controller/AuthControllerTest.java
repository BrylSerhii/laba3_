package com.srgblr.laba3.security.http.controller;

import com.srgblr.laba3.security.dto.LoginDto;
import com.srgblr.laba3.security.dto.RegistrationDto;
import com.srgblr.laba3.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestConfig.class)  // Імпортуємо конфіг з моками
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;  // Впроваджений мок із TestConfig

    @Test
    void loginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void login_Success_ShouldRedirectToRoot() throws Exception {
        Cookie cookie = new Cookie("token", "abc123");
        ResponseEntity<Cookie> responseEntity = new ResponseEntity<>(cookie, HttpStatus.OK);

        Mockito.when(authService.login(any(LoginDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/auth/login")
                        .param("email", "test@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_UserNotFound_ShouldReturnLoginWithError() throws Exception {
        ResponseEntity<Cookie> responseEntity = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        Mockito.when(authService.login(any(LoginDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/auth/login")
                        .param("email", "missing@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "No user with such email"));
    }

    @Test
    void registrationPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/auth/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void register_Success_ShouldRedirectToLogin() throws Exception {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.CREATED);

        Mockito.when(authService.register(any(RegistrationDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/auth/registration")
                        .param("email", "newuser@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void register_Failure_ShouldReturnRegisterWithErrors() throws Exception {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Email already taken", HttpStatus.BAD_REQUEST);

        Mockito.when(authService.register(any(RegistrationDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/auth/registration")
                        .param("email", "duplicate@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attribute("errors", org.hamcrest.Matchers.contains("Email already taken")));
    }

    // Тестова конфігурація для моків
    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }
}
