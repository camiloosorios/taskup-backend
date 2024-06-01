package com.uptask.api.Services;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.TaskDTO;
import com.uptask.api.models.Project;
import com.uptask.api.models.Task;

import java.util.List;

public interface TaskService {

    void createTask(ProjectDTO projectDTO, TaskDTO taskDTO);

    List<TaskDTO> getProjectTasks(ProjectDTO projectDTO);

    TaskDTO getTaskById(ProjectDTO projectDTO, String taskId);

    void updateTask(TaskDTO taskDTO, Task task, ProjectDTO projectDTO);

    void deleteTask(ProjectDTO projectDTO, String taskId);

    void updateTaskStatus(String status, TaskDTO taskDTO, ProjectDTO projectDTO);

    void modifyNotes(Task task);

    void deleteProjectTasks(Project project);

}
