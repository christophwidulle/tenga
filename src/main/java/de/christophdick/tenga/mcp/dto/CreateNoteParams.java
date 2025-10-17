package de.christophdick.tenga.mcp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateNoteParams {

    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 1048576, message = "Content cannot exceed 1MB")
    private String content;

    private List<String> tags;

    private boolean suggestTags = false;

    public CreateNoteParams() {
    }

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isSuggestTags() {
        return suggestTags;
    }

    public void setSuggestTags(boolean suggestTags) {
        this.suggestTags = suggestTags;
    }
}
