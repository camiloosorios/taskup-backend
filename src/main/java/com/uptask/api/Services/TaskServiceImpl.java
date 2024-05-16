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
import java.util.Arrays;
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
        validateTaskFields(taskDTO);
        Project project = createProjectFromFTO(projectDTO);
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
        Project project = createProjectFromFTO(projectDTO);
        List<TaskDTO> tasksDTO = new ArrayList<>();
        tasks.forEach(task -> {
            TaskDTO taskDTO = createTaskDTO(task, project);
            tasksDTO.add(taskDTO);
        });

        return tasksDTO;
    }

    @Override
    public TaskDTO getTaskById(ProjectDTO projectDTO, String taskId) {
        if (projectDTO.getTasks() == null) {
            throw new RuntimeException("Acción inválida");
        }
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            return null;
        }
        Task task = taskOptional.get();
        boolean taskIsPresent = projectDTO.getTasks().stream().anyMatch(taskDTO -> taskDTO.getId().equals(task.getId()));
        if (!taskIsPresent) {
            throw new RuntimeException("Acción inválida");
        }
        Project project = createProjectFromFTO(projectDTO);
        project.setTasks(null);
        return createTaskDTO(task, project);
    }

    @Override
    @Transactional
    public void updateTask(String taskId, TaskDTO taskDTO, Task task) {
        validateTaskFields(taskDTO);
        try {
            task.setName(taskDTO.getName());
            task.setDescription(taskDTO.getDescription());
            task.setUpdatedAt(LocalDate.now());
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error al Actualizar Tarea");
        }
    }

    @Override
    @Transactional
    public void deleteTask(ProjectDTO projectDTO, String taskId) {
        if (projectDTO.getTasks() == null) {
            throw new RuntimeException("Acción inválida");
        }
        boolean taskIsPresent = projectDTO.getTasks()
                .stream()
                .anyMatch(taskDTO -> taskDTO.getId().equals(taskId));
        if (!taskIsPresent) {
            throw new RuntimeException("Acción inválida");
        }
        try {
            taskRepository.deleteById(taskId);
            List<Task> updatedTasks = projectDTO.getTasks().stream()
                        .filter(task -> !task.getId().equals(taskId))
                        .toList();
            projectDTO.setTasks(updatedTasks);
            projectService.updateProject(projectDTO.getId(), projectDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error al Eliminar Tarea");
        }
    }

    @Override
    @Transactional
    public void updateTaskStatus(String taskId, String status, Task task) {
        if (status == null) {
            throw new RuntimeException("El Estado de la tarea es Obligatorio");
        }
        boolean isValidStatus = Arrays.stream(Status.values())
                .anyMatch(value -> value.getValue().equals(status));
        if (!isValidStatus) {
            throw new RuntimeException("Estado No Válido");
        }
        task.setStatus(status);
        task.setUpdatedAt(LocalDate.now());
        try {
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error al Actualizar Estado de Tarea");
        }
    }

    private Project createProjectFromFTO(ProjectDTO projectDTO) {
        return Project.builder()
                .id(projectDTO.getId())
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(projectDTO.getTasks())
                .build();
    }

    private TaskDTO createTaskDTO(Task task, Project project) {
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

    private void validateTaskFields(TaskDTO taskDTO) {
        if (taskDTO.getName() == null || taskDTO.getName().isBlank()) {
            throw new RuntimeException("El Nombre de la tarea es Obligatorio");
        }
        if (taskDTO.getDescription() == null || taskDTO.getDescription().isBlank()) {
            throw new RuntimeException("La Descripción de la tarea es Obligatorio");
        }
    }
}
