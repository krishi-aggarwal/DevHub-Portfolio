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
        return repo.save(toProject(null, r));
    }

    public Project update(String id, ProjectRequest r) {
        findById(id);
        ensureTitleFree(r.title(), id);
        return repo.save(toProject(id, r));
    }

    private Project toProject(String id, ProjectRequest r) {
        List<String> tech = r.techStack() == null ? List.of()
                : r.techStack().stream().map(String::trim).distinct().toList();
        return new Project(id, r.title().trim(), r.description().trim(),
                tech, clean(r.githubUrl()), clean(r.liveUrl()));
    }

    private static String clean(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private void ensureTitleFree(String title, String currentId) {
        String t = title.trim();
        boolean takenByAnother = repo.findAllByTitleIgnoreCase(t).stream()
                .anyMatch(existing -> !existing.id().equals(currentId));
        if (takenByAnother) {
            throw new ConflictException("A project titled \"" + t + "\" already exists");
        }
    }

    public void delete(String id) {
        findById(id);
        repo.deleteById(id);
    }
}