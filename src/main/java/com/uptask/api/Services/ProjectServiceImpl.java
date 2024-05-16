package com.uptask.api.Services;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.Repositories.ProjectRepository;
import com.uptask.api.models.Project;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
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
        Project project = Project.builder()
                .projectName(projectDTO.getProjectName())
                .clientName(projectDTO.getClientName())
                .description(projectDTO.getDescription())
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
            e.printStackTrace();
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
        if (projectDTO.getProjectName() == null) {
            throw new RuntimeException("El Nombre del Proyecto es Obligatorio");
        }
        if (projectDTO.getClientName() == null) {
            throw new RuntimeException("El Nombre del Cliente es Obligatorio");
        }
        if (projectDTO.getDescription() == null) {
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
                .build();
    }
}
