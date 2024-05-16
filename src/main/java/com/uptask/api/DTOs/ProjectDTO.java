package com.uptask.api.DTOs;

import com.uptask.api.models.Task;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProjectDTO {

    private String id;

    private String projectName;

    private String clientName;

    private String description;

    List<Task> tasks;


    public void addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
            this.tasks.add(task);
    }

}
