package com.krishi.portfolio.devhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProjectRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be at most 100 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        @Size(max = 10, message = "Add at most 10 technologies")
        List<@NotBlank(message = "Technology names cannot be blank")
        @Size(max = 30, message = "Each technology must be at most 30 characters") String> techStack,

        @Size(max = 300, message = "GitHub URL is too long")
        @Pattern(regexp = "^$|^https?://\\S+$", message = "GitHub URL must start with http:// or https://")
        String githubUrl,

        @Size(max = 300, message = "Live URL is too long")
        @Pattern(regexp = "^$|^https?://\\S+$", message = "Live URL must start with http:// or https://")
        String liveUrl) {}