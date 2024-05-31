package com.uptask.api.controllers;

import com.uptask.api.DTOs.NoteDTO;
import com.uptask.api.DTOs.TaskDTO;
import com.uptask.api.Services.NoteService;
import com.uptask.api.models.Task;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody NoteDTO noteDTO, HttpServletRequest request) {
        String userId = getAuthenticatedUser();
        Task task = (Task) request.getAttribute("task");
        try {
            noteService.createNote(userId, task, noteDTO);

            return ResponseEntity.ok().body("Nota creada");
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public List<NoteDTO> getTaskNotes(HttpServletRequest request) {
        Task task = (Task) request.getAttribute("task");
        List<NoteDTO> notes = new ArrayList<>();
        if (task.getNotes() != null) {
            task.getNotes().forEach(note -> {
                NoteDTO noteDTO = NoteDTO.builder()
                        .id(note.getId())
                        .content(note.getContent())
                        .task(note.getTask())
                        .createdBy(note.getCreatedBy())
                        .createdAt(note.getCreatedAt())
                        .updatedAt(note.getUpdatedAt())
                        .build();
                notes.add(noteDTO);
            });
        }
        return notes;
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable String noteId, HttpServletRequest request) {
        String userId = getAuthenticatedUser();
        Task task = (Task) request.getAttribute("task");
        try {
            noteService.deleteNote(userId, noteId, task);

            return ResponseEntity.ok().body("Nota eliminada");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    private String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getDetails();
    }
}
