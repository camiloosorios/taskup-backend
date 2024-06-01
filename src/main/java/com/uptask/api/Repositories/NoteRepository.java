package com.uptask.api.Repositories;

import com.uptask.api.models.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    @Query(value = "{'task': ?0 }", delete = true)
    void deleteByTask(String task);

}
