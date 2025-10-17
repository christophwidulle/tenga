package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.CreateTagRequest;
import de.christophdick.tenga.dto.TagDTO;
import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.Tag;
import de.christophdick.tenga.exception.ResourceNotFoundException;
import de.christophdick.tenga.exception.ValidationException;
import de.christophdick.tenga.repository.DocumentRepository;
import de.christophdick.tenga.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private static final int MAX_HIERARCHY_DEPTH = 5;

    private final TagRepository tagRepository;
    private final DocumentRepository documentRepository;

    public TagServiceImpl(TagRepository tagRepository, DocumentRepository documentRepository) {
        this.tagRepository = tagRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public TagDTO createTag(CreateTagRequest request) {
        Tag parent = null;

        // Validate and fetch parent if specified
        if (request.getParentId() != null) {
            parent = tagRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent tag", request.getParentId()));

            // Check hierarchy depth
            validateHierarchyDepth(parent);
        }

        // Check for duplicate tag name under same parent
        if (parent != null) {
            tagRepository.findByNameAndParent(request.getName(), parent)
                .ifPresent(existing -> {
                    throw new ValidationException("Tag with name '" + request.getName() + "' already exists under the specified parent");
                });
        } else {
            tagRepository.findByNameAndParentIsNull(request.getName())
                .ifPresent(existing -> {
                    throw new ValidationException("Root tag with name '" + request.getName() + "' already exists");
                });
        }

        // Create new tag
        Tag tag = new Tag(request.getName(), parent);
        tag = tagRepository.save(tag);

        return convertToDTO(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        return convertToDTO(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> listTags() {
        return tagRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> getTagHierarchy() {
        List<Tag> rootTags = tagRepository.findAllRootTags();
        return rootTags.stream()
            .map(this::convertToDTOWithChildren)
            .collect(Collectors.toList());
    }

    @Override
    public TagDTO updateTag(Long id, String newName) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", id));

        // Check for duplicate name under same parent
        if (tag.getParent() != null) {
            tagRepository.findByNameAndParent(newName, tag.getParent())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ValidationException("Tag with name '" + newName + "' already exists under the specified parent");
                    }
                });
        } else {
            tagRepository.findByNameAndParentIsNull(newName)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ValidationException("Root tag with name '" + newName + "' already exists");
                    }
                });
        }

        tag.setName(newName);
        tag = tagRepository.save(tag);

        return convertToDTO(tag);
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", id));

        // Check if tag has documents
        if (tagRepository.hasDocuments(tag)) {
            throw new ValidationException("Cannot delete tag '" + tag.getName() + "' because it is associated with documents");
        }

        // Check if tag has children
        if (tag.hasChildren()) {
            throw new ValidationException("Cannot delete tag '" + tag.getName() + "' because it has child tags");
        }

        tagRepository.delete(tag);
    }

    @Override
    public void associateTagWithDocument(Long tagId, Long documentId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", tagId));

        Document document = documentRepository.findActiveById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        document.addTag(tag);
        documentRepository.save(document);
    }

    @Override
    public void removeTagFromDocument(Long tagId, Long documentId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", tagId));

        Document document = documentRepository.findActiveById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        document.removeTag(tag);
        documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> searchTags(String searchTerm) {
        return tagRepository.searchByName(searchTerm).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Helper methods

    private void validateHierarchyDepth(Tag parent) {
        int depth = 0;
        Tag current = parent;

        while (current != null && depth < MAX_HIERARCHY_DEPTH) {
            depth++;
            current = current.getParent();
        }

        if (depth >= MAX_HIERARCHY_DEPTH) {
            throw new ValidationException("Tag hierarchy cannot exceed " + MAX_HIERARCHY_DEPTH + " levels");
        }
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setCreatedAt(tag.getCreatedAt());

        if (tag.getParent() != null) {
            dto.setParentId(tag.getParent().getId());
            dto.setParentName(tag.getParent().getName());
        }

        // Count documents
        Long documentCount = tagRepository.countDocuments(tag);
        dto.setDocumentCount(documentCount);

        return dto;
    }

    private TagDTO convertToDTOWithChildren(Tag tag) {
        TagDTO dto = convertToDTO(tag);

        // Recursively convert children
        if (tag.hasChildren()) {
            List<TagDTO> childrenDTOs = new ArrayList<>();
            for (Tag child : tag.getChildren()) {
                childrenDTOs.add(convertToDTOWithChildren(child));
            }
            dto.setChildren(childrenDTOs);
        }

        return dto;
    }
}
