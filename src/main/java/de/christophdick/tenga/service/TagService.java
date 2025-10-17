package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.CreateTagRequest;
import de.christophdick.tenga.dto.TagDTO;

import java.util.List;

public interface TagService {

    /**
     * Create a new tag
     */
    TagDTO createTag(CreateTagRequest request);

    /**
     * Get tag by ID
     */
    TagDTO getTagById(Long id);

    /**
     * List all tags (flat list)
     */
    List<TagDTO> listTags();

    /**
     * Get tag hierarchy (tree structure)
     */
    List<TagDTO> getTagHierarchy();

    /**
     * Update tag (rename)
     */
    TagDTO updateTag(Long id, String newName);

    /**
     * Delete tag
     */
    void deleteTag(Long id);

    /**
     * Associate tag with document
     */
    void associateTagWithDocument(Long tagId, Long documentId);

    /**
     * Remove tag from document
     */
    void removeTagFromDocument(Long tagId, Long documentId);

    /**
     * Search tags by name
     */
    List<TagDTO> searchTags(String searchTerm);
}
