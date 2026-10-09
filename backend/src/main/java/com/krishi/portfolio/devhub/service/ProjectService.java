package com.krishi.portfolio.devhub.service;

import com.krishi.portfolio.devhub.dto.ProjectRequest;
import com.krishi.portfolio.devhub.exception.ConflictException;
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
        ensureTitleFree(r.title(), null);
        return repo.save(new Project(null, r.title().trim(), r.description(),
                r.techStack(), r.githubUrl(), r.liveUrl()));
    }

    public Project update(String id, ProjectRequest r) {
        findById(id); // 404 if missing
        ensureTitleFree(r.title(), id);
        return repo.save(new Project(id, r.title().trim(), r.description(),
                r.techStack(), r.githubUrl(), r.liveUrl()));
    }

    private void ensureTitleFree(String title, String currentId) {
        repo.findByTitleIgnoreCase(title.trim()).ifPresent(existing -> {
            if (!existing.id().equals(currentId)) {
                throw new ConflictException("A project titled \"" + title.trim() + "\" already exists");
            }
        });
    }

    public void delete(String id) {
        findById(id);
        repo.deleteById(id);
    }
}