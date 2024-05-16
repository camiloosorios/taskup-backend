package com.uptask.api.Repositories;

import com.uptask.api.models.Task;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, String> {

    @Query("{'project.id': ?0}")
    List<Task> findByProject(String project);

}
