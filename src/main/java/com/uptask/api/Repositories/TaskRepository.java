package com.uptask.api.Repositories;

import com.uptask.api.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    @Query("{'project': ?0}")
    List<Task> findByProject(String project);

    @Query(value = "{'project': ?0}", delete = true)
    void deleteByProject(String project);

}
