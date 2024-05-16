package com.uptask.api.Services;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.TaskDTO;
import com.uptask.api.Repositories.TaskRepository;
import com.uptask.api.enums.Status;
import com.uptask.api.models.Project;
import com.uptask.api.models.Task;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ProjectService projectService;

    @Override
    @Transactional
    public void createTask(ProjectDTO projectDTO, TaskDTO taskDTO) {
        if (taskDTO.getName() == null || taskDTO.getName().isBlank()) {
            throw new RuntimeException("El Nombre de la tarea es Obligatorio");
        }
        if (taskDTO.getDescription() == null || taskDTO.getDescription().isBlank()) {
            throw new RuntimeException("La Descripción de la tarea es Obligatorio");
        }
        Project project = Project.builder()
                .id(projectDTO.getId())
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(projectDTO.getTasks())
                .build();
        Task task = Task.builder()
                .name(taskDTO.getName())
                .description(taskDTO.getDescription())
                .status(Status.PENDING.getValue())
                .project(project)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        try {
            taskRepository.save(task);
            projectDTO.addTask(task);
            projectService.updateProject(projectDTO.getId(), projectDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al Crear Tarea");
        }
    }

    @Override
    public List<TaskDTO> getProjectTasks(ProjectDTO projectDTO) {
        List<Task> tasks =  taskRepository.findByProject(projectDTO.getId());
        if (tasks == null) {
            return null;
        }
        Project project = Project.builder()
                .id(projectDTO.getId())
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(projectDTO.getTasks())
                .build();
        List<TaskDTO> tasksDTO = new ArrayList<>();
        tasks.forEach(task -> {
            TaskDTO taskDTO = TaskDTO.builder()
                    .id(task.getId())
                    .name(task.getName())
                    .description(task.getDescription())
                    .status(task.getStatus())
                    .project(project)
                    .createdAt(task.getCreatedAt())
                    .updatedAt(task.getUpdatedAt())
                    .build();
            tasksDTO.add(taskDTO);
        });

        return tasksDTO;
    }

    @Override
    public TaskDTO getTaskById(ProjectDTO projectDTO, String taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Tarea No Encontrada");
        }
        Task task = taskOptional.get();
        Project project = Project.builder()
                .id(projectDTO.getId())
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(projectDTO.getTasks())
                .build();
        return TaskDTO.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .project(project)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void updateTask(ProjectDTO projectDTO, String taskId, TaskDTO taskDTO) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Tarea No Encontrada");
        }
        if (taskDTO.getName() == null || taskDTO.getName().isBlank()) {
            throw new RuntimeException("El Nombre de la tarea es Obligatorio");
        }
        if (taskDTO.getDescription() == null || taskDTO.getDescription().isBlank()) {
            throw new RuntimeException("La Descripción de la tarea es Obligatorio");
        }
        try {
            Task task = taskOptional.get();
            task.setName(taskDTO.getName());
            task.setDescription(taskDTO.getDescription());
            task.setUpdatedAt(LocalDate.now());
            if (taskDTO.getStatus() != null) {
                task.setStatus(taskDTO.getStatus());
            }
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error al Actualizar Tarea");
        }
    }
}
