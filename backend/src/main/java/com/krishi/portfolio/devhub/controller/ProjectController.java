package com.krishi.portfolio.devhub.controller;

import com.krishi.portfolio.devhub.dto.ProjectRequest;
import com.krishi.portfolio.devhub.model.Project;
import com.krishi.portfolio.devhub.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) { this.service = service; }

    @GetMapping
    public List<Project> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Project one(@PathVariable String id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@Valid @RequestBody ProjectRequest r) { return service.create(r); }

    @PutMapping("/{id}")
    public Project update(@PathVariable String id, @Valid @RequestBody ProjectRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }
}