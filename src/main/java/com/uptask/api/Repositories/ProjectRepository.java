package com.uptask.api.Repositories;

import com.uptask.api.models.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByManagerOrTeamContains(String managerId, String userId);

}
