package com.uptask.api.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NoteDTO {

    private String id;

    private String createdBy;

    private String task;

    private String content;

    private LocalDate createdAt;

    private LocalDate updatedAt;

}
