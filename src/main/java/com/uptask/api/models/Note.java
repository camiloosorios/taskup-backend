package com.uptask.api.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "notes")
public class Note {

    @Id
    private String id;

    @NotBlank
    private String createdBy;

    @NotBlank
    private String task;

    @NotBlank
    private String content;

    private LocalDate createdAt;

    private LocalDate updatedAt;

}
