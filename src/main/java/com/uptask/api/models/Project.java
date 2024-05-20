package com.uptask.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
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

}
