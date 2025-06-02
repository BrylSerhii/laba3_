package com.srgblr.laba3.security.http.filter;

import com.srgblr.laba3.security.database.entity.User;
import com.srgblr.laba3.security.service.JwtService;
import com.srgblr.laba3.security.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserService userService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldSetAuthenticationWhenValidToken() throws Exception {
        // Arrange
        String token = "valid-token";
        Cookie cookie = new Cookie("accessToken", token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");

        User user = new User();
        user.setEmail("user@example.com");
        when(user.getAuthorities()).thenReturn(Collections.emptyList());
        when(userService.findByEmail("user@example.com")).thenReturn(user);

        // Act
        jwtAuthenticationFilter.doFilter(request, response, filterChain); // ← тут!

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
