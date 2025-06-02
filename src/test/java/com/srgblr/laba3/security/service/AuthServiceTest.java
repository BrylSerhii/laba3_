package com.srgblr.laba3.security.service;

import com.srgblr.laba3.security.database.entity.Role;
import com.srgblr.laba3.security.database.entity.User;
import com.srgblr.laba3.security.dto.LoginDto;
import com.srgblr.laba3.security.dto.RegistrationDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AuthServiceTest {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authenticationManager = mock(AuthenticationManager.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userService, authenticationManager, passwordEncoder, jwtService);
    }

    @Test
    void loginSuccess() {
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        User user = User.builder().email("test@example.com").password("hashed").build();
        Cookie cookie = new Cookie("token", "jwt");

        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(jwtService.createCookieWithKey(user)).thenReturn(cookie);

        ResponseEntity<Cookie> response = authService.login(loginDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cookie, response.getBody());
    }

    @Test
    void loginUnauthorized() {
        LoginDto loginDto = new LoginDto("test@example.com", "wrongpassword");
        User user = User.builder().email("test@example.com").password("hashed").build();

        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<Cookie> response = authService.login(loginDto);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void loginUserNotFound() {
        when(userService.findByEmail("unknown@example.com")).thenReturn(null);
        ResponseEntity<Cookie> response = authService.login(new LoginDto("unknown@example.com", "pass"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void registerSuccess() {
        RegistrationDto dto = new RegistrationDto("new@example.com", "password", "Name", 180, 190, 200);

        when(userService.findByEmail(dto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-password");

        ResponseEntity<String> response = authService.register(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService).save(argThat(user ->
                user.getEmail().equals(dto.getEmail()) &&
                        user.getFullName().equals(dto.getFullName()) &&
                        user.getRole() == Role.USER
        ));
    }

    @Test
    void registerEmailConflict() {
        RegistrationDto dto = new RegistrationDto("used@example.com", "password", "Name", 180, 190, 200);
        when(userService.findByEmail(dto.getEmail())).thenReturn(new User());

        ResponseEntity<String> response = authService.register(dto);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("email already in use", response.getBody());
    }
}
