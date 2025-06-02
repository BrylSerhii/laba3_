package com.srgblr.laba3.api.http.controller;

import com.srgblr.laba3.api.dto.FacultyCreateDto;
import com.srgblr.laba3.api.service.ApplicationService;
import com.srgblr.laba3.api.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    private final ApplicationService applicationService;

    @GetMapping("/faculty")
    public String getFaculties(Model model) {
        model.addAttribute("faculties", facultyService.getAll());
        return "faculties";
    }

    @GetMapping("/faculty/{id}")
    public String getFaculty(Model model, @PathVariable Long id, Principal principal) {
        model.addAttribute("faculty", facultyService.getById(id));
        model.addAttribute("hasApplied", applicationService.hasApplied(id, principal.getName()).getBody());
        return "faculty";
    }

    @GetMapping("/admin/faculty")
    public String getAdminFaculties(Model model) {
        model.addAttribute("faculties", facultyService.getAll());
        return "admin/faculties";
    }

    @GetMapping("/admin/faculty/{id}")
    public String getAdminFaculty(Model model, @PathVariable Long id) {
        model.addAttribute("faculty", facultyService.getById(id));
        return "admin/faculty";
    }

    @PostMapping("/admin/faculty")
    public String createFaculty(@ModelAttribute FacultyCreateDto fcd) {
        var id = facultyService.create(fcd);
        return "redirect:/admin/faculty/" + id.getBody();
    }

    @PostMapping("/admin/faculty/{id}")
    public String finishDraft(@PathVariable Long id, Model model) {
        var res = facultyService.finishDraftById(id);
        if (res.getStatusCode() != HttpStatus.OK) {
            model.addAttribute("error", res.getBody());
        }
        return "redirect:/admin/faculty/" + id;
    }
}

