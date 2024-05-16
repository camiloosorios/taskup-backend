package com.uptask.api.DTOs;

import com.uptask.api.models.Project;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TaskDTO {

    private String id;

    private String name;

    private String description;

    private String status;

    private Project project;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
