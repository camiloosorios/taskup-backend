package com.uptask.api.Interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.TaskDTO;
import com.uptask.api.Services.TaskService;
import com.uptask.api.models.Task;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class TaskExistInterceptor implements HandlerInterceptor {

    @Autowired
    private TaskService taskService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
        String taskId = request.getRequestURI().split("/")[5];
        TaskDTO taskDTO = taskService.getTaskById(projectDTO, taskId);
        if (taskDTO == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            Map<String, String> error = new HashMap<>();
            error.put("Error", "Tarea no encontrada");
            ObjectMapper objectMapper = new ObjectMapper();
            String errorMessage = objectMapper.writeValueAsString(error);
            PrintWriter writer = response.getWriter();
            writer.write(errorMessage);
            writer.flush();

            return false;
        }
        Task task = Task.builder()
                .id(taskDTO.getId())
                .name(taskDTO.getName())
                .description(taskDTO.getDescription())
                .status(taskDTO.getStatus())
                .project(taskDTO.getProject())
                .createdAt(taskDTO.getCreatedAt())
                .updatedAt(taskDTO.getUpdatedAt())
                .build();
        request.setAttribute("task", task);

        return true;
    }
}
