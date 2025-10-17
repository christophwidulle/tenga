package de.christophdick.tenga.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public class UpdateDocumentRequest {

    @Size(max = 500, message = "Title cannot exceed 500 characters")
    private String title;

    @Size(max = 1048576, message = "Content cannot exceed 1MB")
    private String content;

    private Set<String> tags;

    @Size(max = 1000, message = "Change summary cannot exceed 1000 characters")
    private String changeSummary;

    // Constructors
    public UpdateDocumentRequest() {
    }

    public UpdateDocumentRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }
}
