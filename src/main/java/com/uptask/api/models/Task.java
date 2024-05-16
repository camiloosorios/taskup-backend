package com.uptask.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private String status;

    @DBRef
    @JsonIgnore
    private Project project;

    private LocalDate createdAt;

    private LocalDate updatedAt;

}
