package com.srgblr.laba3.api.database.repository;

import com.srgblr.laba3.api.database.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByUserIdAndFacultyId(Long userId, Long facultyId);

    List<Application> findAllByUserId(Long userId);

    List<Application> findAllByFacultyIdOrderByAvgScoreDesc(Long facultyId);
}
