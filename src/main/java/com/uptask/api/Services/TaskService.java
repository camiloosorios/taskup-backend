package com.uptask.api.Services;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.TaskDTO;

import java.util.List;

public interface TaskService {

    void createTask(ProjectDTO projectDTO, TaskDTO taskDTO);

    List<TaskDTO> getProjectTasks(ProjectDTO projectDTO);

    TaskDTO getTaskById(ProjectDTO projectDTO, String taskId);

    void updateTask(ProjectDTO projectDTO, String taskId, TaskDTO taskDTO);

}
