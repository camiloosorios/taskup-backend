package com.uptask.api.Services.impl;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.TaskDTO;
import com.uptask.api.DTOs.UserDTO;
import com.uptask.api.Repositories.TaskRepository;
import com.uptask.api.Services.NoteService;
import com.uptask.api.Services.ProjectService;
import com.uptask.api.Services.TaskService;
import com.uptask.api.Services.UserService;
import com.uptask.api.enums.Status;
import com.uptask.api.models.CompletedBy;
import com.uptask.api.models.Project;
import com.uptask.api.models.Task;
import com.uptask.api.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private NoteService noteService;

    @Override
    @Transactional
    public void createTask(ProjectDTO projectDTO, TaskDTO taskDTO) {
        validateTaskFields(taskDTO);
        Project project = createProjectFromDTO(projectDTO);
        Task task = Task.builder()
                .name(taskDTO.getName())
                .description(taskDTO.getDescription())
                .status(Status.PENDING.getValue())
                .project(project.getId())
                .completedBy(null)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        try {
            hasAuthorization(projectDTO.getManager());
            taskRepository.save(task);
            projectDTO.addTask(task);
            projectService.updateProject(projectDTO.getId(), projectDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error al Crear Tarea");
        }
    }

    @Override
    public List<TaskDTO> getProjectTasks(ProjectDTO projectDTO) {
        List<Task> tasks =  taskRepository.findByProject(projectDTO.getId());
        if (tasks == null) {
            return null;
        }
        Project project = createProjectFromDTO(projectDTO);
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
        Project project = createProjectFromDTO(projectDTO);
        project.setTasks(null);
        return createTaskDTO(task, project);
    }

    @Override
    @Transactional
    public void updateTask(TaskDTO taskDTO, Task task, ProjectDTO projectDTO) {
        validateTaskFields(taskDTO);
        try {
            hasAuthorization(projectDTO.getManager());
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
            hasAuthorization(projectDTO.getManager());
            noteService.deleteTaskNotes(taskId);
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
    public void updateTaskStatus(String status, TaskDTO taskDTO, ProjectDTO projectDTO) {
        if (status == null) {
            throw new RuntimeException("El Estado de la tarea es Obligatorio");
        }
        boolean isValidStatus = Arrays.stream(Status.values())
                .anyMatch(value -> value.getValue().equals(status));
        if (!isValidStatus) {
            throw new RuntimeException("Estado No Válido");
        }
        try {
            List<CompletedBy> completedByUpdated = new ArrayList<>();
            if (taskDTO.getCompletedBy() != null) {
                taskDTO.getCompletedBy().forEach(userChange -> {
                    completedByUpdated.add(CompletedBy.builder()
                                    .user(userChange.getUser())
                                    .status(userChange.getStatus())
                            .build());
                });
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = (String) authentication.getDetails();
            User user = userService.findUserById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            UserDTO userDTO = UserDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();

            completedByUpdated.add(CompletedBy.builder()
                    .user(userDTO)
                    .status(status)
                    .build());

            Task task = Task.builder()
                    .id(taskDTO.getId())
                    .name(taskDTO.getName())
                    .description(taskDTO.getDescription())
                    .status(status)
                    .project(taskDTO.getProject())
                    .completedBy(completedByUpdated)
                    .createdAt(taskDTO.getCreatedAt())
                    .updatedAt(LocalDate.now())
                    .build();

            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error al Actualizar Estado de Tarea");
        }
    }

    @Override
    @Transactional
    @Async
    public void modifyNotes(Task task) {
        taskRepository.save(task);
    }

    private Project createProjectFromDTO(ProjectDTO projectDTO) {
        return Project.builder()
                .id(projectDTO.getId())
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(projectDTO.getTasks())
                .manager(projectDTO.getManager())
                .build();
    }

    @Override
    @Transactional
    public void deleteProjectTasks(Project project) {
        try {
            taskRepository.deleteByProject(project.getId());
            if (project.getTasks() != null && !project.getTasks().isEmpty()) {
                project.getTasks().forEach(task -> {
                    noteService.deleteTaskNotes(task.getId());
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al Eliminar Tareas del Proyecto");
        }
    }

    private TaskDTO createTaskDTO(Task task, Project project) {
        List<CompletedBy> completedByUpdated = new ArrayList<>();
        if (task.getCompletedBy()!= null) {
            task.getCompletedBy().forEach(user -> {
                completedByUpdated.add(CompletedBy.builder()
                                .user(user.getUser())
                                .status(user.getStatus())
                       .build());
            });
        }
        return TaskDTO.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .project(project.getId())
                .completedBy(completedByUpdated)
                .notes(task.getNotes())
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

    private void hasAuthorization(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String managerId = (String) authentication.getDetails();
        if (!userId.equals(managerId)) {
            throw new RuntimeException("Acción inválida");
        }
    }
}
