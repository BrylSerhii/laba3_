package com.srgblr.laba3.api.service;

import com.srgblr.laba3.api.database.entity.Faculty;
import com.srgblr.laba3.api.database.repository.FacultyRepository;
import com.srgblr.laba3.api.dto.FacultyCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Transactional
    public List<Faculty> getAll() {
        return facultyRepository.findAll();
    }

    @Transactional
    public ResponseEntity<Long> create(FacultyCreateDto fcd) {
        var facultyToSave = Faculty.builder()
                .name(fcd.getName())
                .capacity(fcd.getCapacity())
                .mathWeight(fcd.getMathWeight())
                .ukrWeight(fcd.getUkrWeight())
                .engWeight(fcd.getEngWeight())
                .draftFinished(false)
                .build();
        var faculty = facultyRepository.save(facultyToSave);
        return new ResponseEntity<>(faculty.getId(), HttpStatus.OK);
    }

    @Transactional
    public Faculty getById(Long id) {
        var optional = facultyRepository.findById(id);
        return optional.orElse(null);
    }

    @Transactional
    public ResponseEntity<String> finishDraftById(Long id) {
        var faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            return new ResponseEntity<>("not found",HttpStatus.NOT_FOUND);
        } else {
            faculty.setDraftFinished(Boolean.TRUE);
            facultyRepository.save(faculty);
            return new ResponseEntity<>("success",HttpStatus.OK);
        }
    }
}
