package com.krishi.portfolio.devhub.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "projects")
public record Project(
        @Id String id,
        String title,
        String description,
        List<String> techStack,
        String githubUrl,
        String liveUrl) {}