package com.uptask.api.Services.impl;

import com.uptask.api.DTOs.NoteDTO;
import com.uptask.api.Repositories.NoteRepository;
import com.uptask.api.Services.NoteService;
import com.uptask.api.Services.TaskService;
import com.uptask.api.models.Note;
import com.uptask.api.models.Task;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TaskService taskService;

    @Override
    @Transactional
    public void createNote(String userId, Task task, NoteDTO noteDTO) {
        if (noteDTO.getContent() == null) {
            throw new RuntimeException("El contenido de la nota es requerido");
        }
        Note note = Note.builder()
                .createdBy(userId)
                .content(noteDTO.getContent())
                .task(task.getId())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        try {
            noteRepository.save(note);
            List<Note> notesUpdated = new ArrayList<>();
            if (task.getNotes() != null) {
                notesUpdated = task.getNotes();
            }
            notesUpdated.add(note);
            task.setNotes(notesUpdated);
            taskService.modifyNotes(task);
        } catch (Exception e) {
            throw new RuntimeException("Error al Crear Nota");
        }
    }

    @Override
    public void deleteNote(String userId, String noteId, Task task) {
        Note noteExists = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("La nota no existe"));
        if (!noteExists.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Acción no válida");
        }

        noteRepository.delete(noteExists);
        List<Note> notesUpdated = task.getNotes().stream().filter(note -> !note.getId().equals(noteId)).toList();
        task.setNotes(notesUpdated);
        taskService.modifyNotes(task);

    }

    @Override
    public void deleteTaskNotes(String task) {
        noteRepository.deleteByTask(task);
    }
}
