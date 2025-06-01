package com.srgblr.laba3.security.service;

import com.srgblr.laba3.security.database.entity.Role;
import com.srgblr.laba3.security.database.entity.User;
import com.srgblr.laba3.security.dto.LoginDto;
import com.srgblr.laba3.security.dto.RegistrationDto;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    @Transactional
    public ResponseEntity<Cookie> login(LoginDto loginDto) {
        var user = userService.findByEmail(loginDto.getEmail());
        if (user != null) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
                var cookie = jwtService.createCookieWithKey(user);
                return new ResponseEntity<>(cookie,HttpStatus.OK);
            } catch (BadCredentialsException e) {
                return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<String> register(RegistrationDto rdto) {
        if (userService.findByEmail(rdto.getEmail()) == null) {
            var user = User.builder()
                    .email(rdto.getEmail())
                    .password(passwordEncoder.encode(rdto.getPassword()))
                    .role(Role.USER)
                    .fullName(rdto.getFullName())
                    .mathScore(rdto.getMathScore())
                    .ukrScore(rdto.getUkrScore())
                    .engScore(rdto.getEngScore())
                    .build();
            userService.save(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("email already in use", HttpStatus.CONFLICT);
        }
    }
}
