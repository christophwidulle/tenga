package de.christophdick.tenga.mcp.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class SearchNotesParams {

    @NotBlank(message = "Search query is required")
    private String query;

    private List<String> tags;

    private Integer page = 0;

    private Integer size = 20;

    private boolean semantic = false;

    public SearchNotesParams() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isSemantic() {
        return semantic;
    }

    public void setSemantic(boolean semantic) {
        this.semantic = semantic;
    }
}
