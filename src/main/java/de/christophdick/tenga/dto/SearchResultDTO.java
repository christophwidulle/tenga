package de.christophdick.tenga.dto;

public class SearchResultDTO {

    private Long documentId;
    private String title;
    private String snippet;
    private Float relevanceScore;

    // Constructors
    public SearchResultDTO() {
    }

    public SearchResultDTO(Long documentId, String title, String snippet, Float relevanceScore) {
        this.documentId = documentId;
        this.title = title;
        this.snippet = snippet;
        this.relevanceScore = relevanceScore;
    }

    // Getters and Setters
    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Float getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Float relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}
