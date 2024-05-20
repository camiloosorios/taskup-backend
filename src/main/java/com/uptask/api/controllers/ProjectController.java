package com.uptask.api.controllers;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.Services.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody ProjectDTO projectDTO) {
        try {
            projectService.createProject(projectDTO);

            return ResponseEntity.ok().body("Producto Creado correctamente");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable String id, HttpServletRequest request) {
        ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
        return ResponseEntity.ok().body(projectDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id, @RequestBody ProjectDTO projectDTO) {
        try {
            projectService.updateProject(id, projectDTO);

            return ResponseEntity.ok().body("Proyecto actualizado");
        } catch (RuntimeException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errors);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        try {
            projectService.deleteProject(id);

            return ResponseEntity.ok().body("Proyecto eliminado");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }
}
