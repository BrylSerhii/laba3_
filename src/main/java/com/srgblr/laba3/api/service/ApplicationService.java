package com.srgblr.laba3.api.service;

import com.srgblr.laba3.api.database.entity.Application;
import com.srgblr.laba3.api.database.repository.ApplicationRepository;
import com.srgblr.laba3.api.dto.ApplicationCreateDto;
import com.srgblr.laba3.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final UserService userService;

    private final FacultyService facultyService;

    @Transactional
    public List<Application> getAll() {
        return applicationRepository.findAll();
    }

    @Transactional
    public ResponseEntity<Long> create(ApplicationCreateDto acd, String email) {
        var user = userService.findByEmail(email);
        var faculty = facultyService.getById(acd.getFacultyId());
        var applicationToSave = Application.builder()
                .user(user)
                .faculty(faculty)
                .priority(acd.getPriority())
                .avgScore(user.getEngScore() * faculty.getEngWeight() +
                        user.getMathScore() * faculty.getMathWeight() +
                        user.getUkrScore() * faculty.getUkrWeight())
                .build();
        var application = applicationRepository.save(applicationToSave);
        return new ResponseEntity<>(application.getId(), HttpStatus.OK);
    }

    public ResponseEntity<Boolean> hasApplied(Long facultyId, String email) {
        var user = userService.findByEmail(email);
        if (user != null) {
            var applied = applicationRepository.findByUserIdAndFacultyId(user.getId(), facultyId).orElse(null) != null;
            return new ResponseEntity<>(applied,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false,HttpStatus.NOT_FOUND);
        }
    }

    public List<Application> getAllByUserEmail(String email) {
        var user = userService.findByEmail(email);
        return applicationRepository.findAllByUserId(user.getId());
    }
}
