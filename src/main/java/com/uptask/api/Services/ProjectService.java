package com.uptask.api.Services;

import com.uptask.api.DTOs.ProjectDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectDTO> getAllProjects();

    void createProject(ProjectDTO projectDTO);

    ProjectDTO getProjectById(String id);

    void updateProject(String id, ProjectDTO projectDTO);

    void deleteProject(String id);

}
