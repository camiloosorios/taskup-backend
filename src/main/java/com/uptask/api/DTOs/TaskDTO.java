package com.uptask.api.DTOs;

import com.uptask.api.models.CompletedBy;
import com.uptask.api.models.Note;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    private String project;

    private List<CompletedBy> completedBy;

    private List<Note> notes;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
