package com.uptask.api.controllers;

import com.uptask.api.DTOs.ProjectDTO;
import com.uptask.api.DTOs.UserDTO;
import com.uptask.api.Services.ProjectService;
import com.uptask.api.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/projects/{projectId}/team")
public class TeamMemberController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @PostMapping("/find")
    public ResponseEntity<?> findMemberByEmail(@RequestBody UserDTO userDTO) {
        try {
            UserDTO user = userService.findUserByEmail(userDTO.getEmail());

            return ResponseEntity.ok().body(user);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> addMemberById(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        try {
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            projectService.addMemberById(userDTO.getId(), projectDTO);

            return ResponseEntity.ok().body("Usuario agregado al equipo");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable String id, HttpServletRequest request) {
        try {
            ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
            projectService.deleteMember(id, projectDTO);

            return ResponseEntity.ok().body("Usuario eliminado del equipo");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public Set<UserDTO> getProjectMembers(HttpServletRequest request) {
        ProjectDTO projectDTO = (ProjectDTO) request.getAttribute("projectDTO");
        Set<UserDTO> projectMembers = new HashSet<>();
        projectDTO.getTeam().forEach(member -> {
            UserDTO userDTO = UserDTO.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .build();

            projectMembers.add(userDTO);
        });

        return projectMembers;
    }
}
