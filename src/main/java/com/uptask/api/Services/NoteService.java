package com.uptask.api.Services;

import com.uptask.api.DTOs.NoteDTO;
import com.uptask.api.models.Task;

public interface NoteService {

    void createNote(String userId, Task task, NoteDTO noteDTO);

    void deleteNote(String userId, String noteId, Task task);

    void deleteTaskNotes(String task);
}
