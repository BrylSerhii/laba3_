package com.srgblr.laba3.api.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "faculty")
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    Float mathWeight;

    Float ukrWeight;

    Float engWeight;

    Integer capacity;

    Boolean draftFinished;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "faculty")
    @OrderBy("avgScore DESC")
    List<Application> applications;
}
