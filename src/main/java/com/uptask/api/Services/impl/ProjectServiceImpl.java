package com.uptask.api.Services.impl;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.Repositories.ProjectRepository;
import com.uptask.api.Services.ProjectService;
import com.uptask.api.Services.TaskService;
import com.uptask.api.Services.UserService;
import com.uptask.api.models.Project;
import com.uptask.api.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private TaskService taskService;

    @Override
    public List<ProjectDTO> getAllProjects() {
        String userId = getAuthenticatedUser();
        List<Project> projects = projectRepository.findByManagerOrTeamContains(userId, userId);
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        projects.forEach(project -> {
            projectDTOs.add(createProjectDTO(project));
        });

        return projectDTOs;
    }

    @Override
    @Transactional
    public void createProject(ProjectDTO projectDTO) {
        validateProjectDTO(projectDTO);
        String managerId = getAuthenticatedUser();
        Project project = Project.builder()
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(new ArrayList<>())
                .team(new HashSet<>())
                .manager(managerId)
                .build();
        try {
            projectRepository.save(project);
        } catch (Exception e) {
            throw new RuntimeException("Error Al Crear Proyecto");
        }
    }

    @Override
    public ProjectDTO getProjectById(String id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            String authenticatedUser = getAuthenticatedUser();
            if (project.getTeam() != null) {
                boolean isMember = project.getTeam().stream().anyMatch(member -> member.getId().equals(authenticatedUser));
                if (!project.getManager().equals(authenticatedUser) && !isMember) {
                    throw new RuntimeException("Acción no válida");
                }
            } else {
                if (!project.getManager().equals(authenticatedUser)) {
                    throw new RuntimeException("Acción no válida");
                }
            }

            return createProjectDTO(project);

        }
        return null;
    }

    @Override
    @Transactional
    public void updateProject(String id, ProjectDTO projectDTO) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new RuntimeException("Proyecto No Encontrado");
        }
        Project project = projectOptional.get();
        String authenticatedUser = getAuthenticatedUser();
        if (!authenticatedUser.equals(project.getManager())) {
            throw new RuntimeException("Acción no válida");
        }
        validateProjectDTO(projectDTO);
        project.setProjectName(projectDTO.getProjectName());
        project.setClientName(projectDTO.getClientName());
        project.setDescription(projectDTO.getDescription());
        if (projectDTO.getTasks() != null) {
            project.setTasks(projectDTO.getTasks());
        }
        try {
            projectRepository.save(project);
        } catch (Exception e) {
            throw new RuntimeException("Error Al Actualizar Proyecto");
        }
    }

    @Override
    @Transactional
    public void deleteProject(String id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new RuntimeException("Proyecto No Encontrado");
        }
        Project project = projectOptional.get();
        String authenticatedUser = getAuthenticatedUser();
        if (!authenticatedUser.equals(project.getManager())) {
            throw new RuntimeException("Acción no válida");
        }
        try {
            taskService.deleteProjectTasks(project);
            projectRepository.delete(project);
        } catch (Exception e) {
            throw new RuntimeException("Error Al Eliminar Proyecto");
        }
    }

    @Override
    @Transactional
    public void addMemberById(String id, ProjectDTO projectDTO) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("Usuario No Encontrado");
        }
        Set<User> teamUpdated = new HashSet<>();
        if (projectDTO.getTeam() != null) {
            teamUpdated = projectDTO.getTeam();
        }
        teamUpdated.stream().anyMatch( team -> {
            if (team.getId().equals(user.getId())) {
                throw new RuntimeException("El Usuario ya existe en el proyecto");
            }
            return false;
        });
        teamUpdated.add(user);
        Project project = Project.builder()
                .id(projectDTO.getId())
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .tasks(projectDTO.getTasks())
                .team(teamUpdated)
                .manager(projectDTO.getManager())
                .build();

        projectRepository.save(project);
    }

    @Override
    public void deleteMember(String id, ProjectDTO projectDTO) {
        try {
            User user = userService.findUserById(id).orElse(null);
            if (user == null) {
                throw new RuntimeException("Usuario No Encontrado");
            }
            boolean userExists = projectDTO.getTeam().stream().anyMatch(member -> member.getId().equals(user.getId()));
            if (!userExists) {
                throw new RuntimeException("El Usuario no existe en el proyecto");
            }
            Set<User> updatedTeam = projectDTO.getTeam().stream()
                    .filter(member -> !member.getId().equals(id)).collect(Collectors.toSet());
            Project project = Project.builder()
                    .id(projectDTO.getId())
                    .projectName(projectDTO.getProjectName())
                    .clientName(projectDTO.getClientName())
                    .description(projectDTO.getDescription())
                    .manager(projectDTO.getManager())
                    .tasks(projectDTO.getTasks())
                    .team(updatedTeam)
                    .build();

            projectRepository.save(project);
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
    }

    private void validateProjectDTO(ProjectDTO projectDTO) {
        if (projectDTO.getProjectName() == null || projectDTO.getProjectName().isBlank()) {
            throw new RuntimeException("El Nombre del Proyecto es Obligatorio");
        }
        if (projectDTO.getClientName() == null || projectDTO.getClientName().isBlank()) {
            throw new RuntimeException("El Nombre del Cliente es Obligatorio");
        }
        if (projectDTO.getDescription() == null || projectDTO.getDescription().isBlank()) {
            throw new RuntimeException("La Descripción del Proyecto es Obligatoria");
        }
    }

    private ProjectDTO createProjectDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .clientName(project.getClientName())
                .description(project.getDescription())
                .tasks(project.getTasks())
                .manager(project.getManager())
                .team(project.getTeam())
                .build();
    }

    private String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getDetails();
    }
}
