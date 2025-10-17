package de.christophdick.tenga.controller;

import de.christophdick.tenga.dto.DocumentVersionDTO;
import de.christophdick.tenga.service.VersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/{documentId}/versions")
public class VersionController {

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentVersionDTO>> getVersionHistory(@PathVariable Long documentId) {
        List<DocumentVersionDTO> versions = versionService.getVersionHistory(documentId);
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{versionNumber}")
    public ResponseEntity<DocumentVersionDTO> getVersion(
            @PathVariable Long documentId,
            @PathVariable Integer versionNumber) {
        DocumentVersionDTO version = versionService.getVersion(documentId, versionNumber);
        return ResponseEntity.ok(version);
    }

    @PostMapping("/{versionNumber}/restore")
    public ResponseEntity<DocumentVersionDTO> restoreVersion(
            @PathVariable Long documentId,
            @PathVariable Integer versionNumber) {
        DocumentVersionDTO version = versionService.restoreVersion(documentId, versionNumber);
        return ResponseEntity.ok(version);
    }

    @GetMapping("/compare")
    public ResponseEntity<String> compareVersions(
            @PathVariable Long documentId,
            @RequestParam Integer v1,
            @RequestParam Integer v2) {
        String diff = versionService.compareVersions(documentId, v1, v2);
        return ResponseEntity.ok(diff);
    }
}
