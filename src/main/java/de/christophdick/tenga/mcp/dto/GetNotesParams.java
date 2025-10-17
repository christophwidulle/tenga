package de.christophdick.tenga.mcp.dto;

import java.util.List;

public class GetNotesParams {

    private Long documentId;

    private List<String> tags;

    private Integer page = 0;

    private Integer size = 20;

    private boolean includeFullContent = false;

    public GetNotesParams() {
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
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

    public boolean isIncludeFullContent() {
        return includeFullContent;
    }

    public void setIncludeFullContent(boolean includeFullContent) {
        this.includeFullContent = includeFullContent;
    }
}
