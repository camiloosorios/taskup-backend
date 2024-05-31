package com.uptask.api.DTOs;

import com.uptask.api.models.Task;
import com.uptask.api.models.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private List<Task> tasks;

    private String manager;

    private Set<User> team;


    public void addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
            this.tasks.add(task);
    }

}
