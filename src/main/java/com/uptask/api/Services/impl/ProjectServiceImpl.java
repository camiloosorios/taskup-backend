package com.uptask.api.Services.impl;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.Repositories.ProjectRepository;
import com.uptask.api.Services.ProjectService;
import com.uptask.api.models.Project;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<ProjectDTO> getAllProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String manager = (String) authentication.getDetails();
        List<Project> projects = projectRepository.findByManager(manager);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        Project project = Project.builder()
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
                .manager(userId)
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = (String) authentication.getDetails();
            if (!project.getManager().equals(userId)) {
                throw new RuntimeException("Acción no válida");
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
        validateProjectDTO(projectDTO);
        Project project = projectOptional.get();
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
        try {
            projectRepository.delete(projectOptional.get());
        } catch (Exception e) {
            throw new RuntimeException("Error Al Eliminar Proyecto");
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
                .build();
    }
}
