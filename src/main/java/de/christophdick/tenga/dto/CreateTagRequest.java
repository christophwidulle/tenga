package de.christophdick.tenga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTagRequest {

    @NotBlank(message = "Tag name cannot be blank")
    @Size(max = 100, message = "Tag name cannot exceed 100 characters")
    private String name;

    private Long parentId;

    // Constructors
    public CreateTagRequest() {
    }

    public CreateTagRequest(String name) {
        this.name = name;
    }

    public CreateTagRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
