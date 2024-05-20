package com.uptask.api.controllers;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.TaskDTO;
import com.uptask.api.Services.TaskService;
import com.uptask.api.models.Project;
import com.uptask.api.models.Task;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskDTO taskDTO, HttpServletRequest request) {
        try {
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            taskService.createTask(projectDTO, taskDTO);

            return ResponseEntity.ok().body("Tarea creada");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(400).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getProjectTasks(HttpServletRequest request) {
        try {
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            List<TaskDTO> tasks = taskService.getProjectTasks(projectDTO);

            return ResponseEntity.ok().body(tasks);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(400).body(error);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable String taskId, HttpServletRequest request) {
        try {
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            TaskDTO task = taskService.getTaskById(projectDTO, taskId);

            return ResponseEntity.ok().body(task);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(400).body(error);
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String taskId, @RequestBody TaskDTO taskDTO, HttpServletRequest request) {
        try {
            Task task = (Task) request.getAttribute("task");
            taskService.updateTask(taskId, taskDTO, task);

            return ResponseEntity.ok().body("Tarea Actualizada Correctamente");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(400).body(error);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskId, HttpServletRequest request) {
        try {
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            taskService.deleteTask(projectDTO, taskId);

            return ResponseEntity.ok().body("Tarea Eliminada Correctamente");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(400).body(error);
        }
    }

    @PostMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@RequestBody Map<String, String> status, HttpServletRequest request) {
        try {
            Task task = (Task) request.getAttribute("task");
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            taskService.updateTaskStatus(status.get("status"), task);

            return ResponseEntity.ok().body("Tarea Actualizada Correctamente");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(400).body(error);
        }
    }

}
