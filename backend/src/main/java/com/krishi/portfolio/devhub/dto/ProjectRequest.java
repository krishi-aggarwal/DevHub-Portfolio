package com.krishi.portfolio.devhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProjectRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 1000) String description,
        List<String> techStack,
        String githubUrl,
        String liveUrl) {}