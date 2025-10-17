package de.christophdick.tenga.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TagDTO {

    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private List<TagDTO> children;
    private Long documentCount;
    private LocalDateTime createdAt;

    // Constructors
    public TagDTO() {
    }

    public TagDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<TagDTO> getChildren() {
        return children;
    }

    public void setChildren(List<TagDTO> children) {
        this.children = children;
    }

    public Long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Long documentCount) {
        this.documentCount = documentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
