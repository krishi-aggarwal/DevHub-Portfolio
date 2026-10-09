package com.krishi.portfolio.devhub.repository;

import com.krishi.portfolio.devhub.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {}