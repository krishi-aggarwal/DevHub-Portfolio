package com.krishi.portfolio.devhub.service;

import com.krishi.portfolio.devhub.dto.ProjectRequest;
import com.krishi.portfolio.devhub.exception.NotFoundException;
import com.krishi.portfolio.devhub.model.Project;
import com.krishi.portfolio.devhub.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) { this.repo = repo; }

    public List<Project> findAll() { return repo.findAll(); }

    public Project findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Project " + id + " not found"));
    }

    public Project create(ProjectRequest r) {
        return repo.save(new Project(null, r.title(), r.description(),
                r.techStack(), r.githubUrl(), r.liveUrl()));
    }

    public Project update(String id, ProjectRequest r) {
        findById(id); // throws 404 if missing
        return repo.save(new Project(id, r.title(), r.description(),
                r.techStack(), r.githubUrl(), r.liveUrl()));
    }

    public void delete(String id) {
        findById(id);
        repo.deleteById(id);
    }
}