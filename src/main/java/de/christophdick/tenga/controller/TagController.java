package de.christophdick.tenga.controller;

import de.christophdick.tenga.dto.CreateTagRequest;
import de.christophdick.tenga.dto.TagDTO;
import de.christophdick.tenga.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody CreateTagRequest request) {
        TagDTO tag = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable Long id) {
        TagDTO tag = tagService.getTagById(id);
        return ResponseEntity.ok(tag);
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> listTags() {
        List<TagDTO> tags = tagService.listTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<List<TagDTO>> getTagHierarchy() {
        List<TagDTO> hierarchy = tagService.getTagHierarchy();
        return ResponseEntity.ok(hierarchy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Long id,
            @RequestParam String name) {
        TagDTO tag = tagService.updateTag(id, name);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagDTO>> searchTags(@RequestParam String q) {
        List<TagDTO> tags = tagService.searchTags(q);
        return ResponseEntity.ok(tags);
    }

    @PostMapping("/{tagId}/documents/{documentId}")
    public ResponseEntity<Void> associateTagWithDocument(
            @PathVariable Long tagId,
            @PathVariable Long documentId) {
        tagService.associateTagWithDocument(tagId, documentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tagId}/documents/{documentId}")
    public ResponseEntity<Void> removeTagFromDocument(
            @PathVariable Long tagId,
            @PathVariable Long documentId) {
        tagService.removeTagFromDocument(tagId, documentId);
        return ResponseEntity.noContent().build();
    }
}
