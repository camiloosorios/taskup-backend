package com.uptask.api.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @NotBlank
    private String projectName;

    @NotBlank
    private String clientName;

    @NotBlank
    private String description;

    @DBRef
    private List<Task> tasks;

    private String manager;

}
