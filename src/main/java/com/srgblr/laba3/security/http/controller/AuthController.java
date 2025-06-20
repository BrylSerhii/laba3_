package com.srgblr.laba3.security.http.controller;


import com.srgblr.laba3.security.dto.LoginDto;
import com.srgblr.laba3.security.dto.RegistrationDto;
import com.srgblr.laba3.security.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto ld, Model model, HttpServletResponse response) {
        var res = authService.login(ld);
        if (res.getBody() != null) {
            response.addCookie(res.getBody());
        }
        if (res.getStatusCode() != HttpStatus.OK) {
            if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                model.addAttribute("error", "No user with such email");
            } else {
                model.addAttribute("error", "Bad credentials");
            }
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/registration")
    public String regPage() {
        return "register";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute RegistrationDto rdd, Model model) {
        var res = authService.register(rdd);
        if (res.getStatusCode() != HttpStatus.CREATED) {
            model.addAttribute("errors", Collections.singletonList(res.getBody()));
            return "register";
        }
        return "redirect:/auth/login";
    }
}
