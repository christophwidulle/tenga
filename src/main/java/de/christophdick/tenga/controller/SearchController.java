package de.christophdick.tenga.controller;

import de.christophdick.tenga.dto.SearchResultDTO;
import de.christophdick.tenga.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<Page<SearchResultDTO>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {

        Page<SearchResultDTO> results;

        if (q != null && !q.isBlank()) {
            results = searchService.fullTextSearch(q, pageable);
        } else {
            results = searchService.combinedSearch(q, tags, startDate, endDate, pageable);
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("/semantic")
    public ResponseEntity<Page<SearchResultDTO>> semanticSearch(
            @RequestParam String q,
            Pageable pageable) {
        Page<SearchResultDTO> results = searchService.semanticSearch(q, pageable);
        return ResponseEntity.ok(results);
    }
}
