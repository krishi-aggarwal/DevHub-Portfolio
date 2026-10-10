package com.krishi.portfolio.devhub.repository;

import com.krishi.portfolio.devhub.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findAllByTitleIgnoreCase(String title);
}