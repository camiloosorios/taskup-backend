package com.uptask.api.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String status;

    @NotBlank
    private String project;

    private List<CompletedBy> completedBy;

    @DBRef
    private List<Note> notes;

    private LocalDate createdAt;

    private LocalDate updatedAt;

}
