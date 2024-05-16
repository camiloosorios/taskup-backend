package com.uptask.api.Interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.Services.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProjectExistInterceptor implements HandlerInterceptor {


    @Autowired
    private ProjectService projectService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String projectId = request.getRequestURI().split("/")[3];
        ProjectDTO projectDTO = projectService.getProjectById(projectId);
        if (projectDTO == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            Map<String, String> error = new HashMap<>();
            error.put("Error", "Proyecto no encontrado");
            ObjectMapper objectMapper = new ObjectMapper();
            String errorMessage = objectMapper.writeValueAsString(error);
            PrintWriter writer = response.getWriter();
            writer.write(errorMessage);
            writer.flush();

            return false;
        }
        request.setAttribute("projectDTO", projectDTO);

        return true;
    }
}
