package com.srgblr.laba3.api.http.controller;

import com.srgblr.laba3.api.dto.ApplicationCreateDto;
import com.srgblr.laba3.api.dto.FacultyCreateDto;
import com.srgblr.laba3.api.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/application")
    public String getApplications(Model model, Principal principal) {
        model.addAttribute("applications", applicationService.getAllByUserEmail(principal.getName()));
        return "applications";
    }

    @PostMapping("/application")
    public String apply(@ModelAttribute ApplicationCreateDto fcd, Principal principal) {
        applicationService.create(fcd, principal.getName());
        return "redirect:/faculty/" + fcd.getFacultyId();
    }
}

