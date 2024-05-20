package com.uptask.api.Repositories;

import com.uptask.api.models.Task;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, String> {

    @Query("{'project': ?0}")
    List<Task> findByProject(String project);

}
