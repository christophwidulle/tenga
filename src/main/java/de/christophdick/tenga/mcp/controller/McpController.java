package de.christophdick.tenga.mcp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.SearchResultDTO;
import de.christophdick.tenga.exception.ResourceNotFoundException;
import de.christophdick.tenga.exception.ValidationException;
import de.christophdick.tenga.mcp.dto.*;
import de.christophdick.tenga.mcp.service.McpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcp")
@Tag(name = "MCP", description = "Model Context Protocol endpoints for chat agent integration")
public class McpController {

    private static final Logger logger = LoggerFactory.getLogger(McpController.class);

    private final McpService mcpService;
    private final ObjectMapper objectMapper;

    public McpController(McpService mcpService, ObjectMapper objectMapper) {
        this.mcpService = mcpService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "JSON-RPC endpoint for MCP methods", description = "Handles all MCP method calls via JSON-RPC 2.0 protocol")
    public ResponseEntity<JsonRpcResponse> handleJsonRpc(@Valid @RequestBody JsonRpcRequest request) {
        logger.info("MCP: Received JSON-RPC request - method: {}, id: {}", request.getMethod(), request.getId());

        try {
            Object result = dispatchMethod(request.getMethod(), request.getParams());
            return ResponseEntity.ok(new JsonRpcResponse(result, request.getId()));
        } catch (ResourceNotFoundException e) {
            logger.error("MCP: Resource not found - {}", e.getMessage());
            JsonRpcError error = new JsonRpcError(
                JsonRpcError.RESOURCE_NOT_FOUND,
                e.getMessage()
            );
            return ResponseEntity.ok(new JsonRpcResponse(error, request.getId()));
        } catch (ValidationException e) {
            logger.error("MCP: Validation error - {}", e.getMessage());
            JsonRpcError error = new JsonRpcError(
                JsonRpcError.VALIDATION_ERROR,
                e.getMessage()
            );
            return ResponseEntity.ok(new JsonRpcResponse(error, request.getId()));
        } catch (IllegalArgumentException e) {
            logger.error("MCP: Invalid parameters - {}", e.getMessage());
            JsonRpcError error = new JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Invalid parameters: " + e.getMessage()
            );
            return ResponseEntity.ok(new JsonRpcResponse(error, request.getId()));
        } catch (UnsupportedOperationException e) {
            logger.error("MCP: Method not found - {}", e.getMessage());
            JsonRpcError error = new JsonRpcError(
                JsonRpcError.METHOD_NOT_FOUND,
                "Method not found: " + request.getMethod()
            );
            return ResponseEntity.ok(new JsonRpcResponse(error, request.getId()));
        } catch (Exception e) {
            logger.error("MCP: Internal error", e);
            JsonRpcError error = new JsonRpcError(
                JsonRpcError.INTERNAL_ERROR,
                "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.ok(new JsonRpcResponse(error, request.getId()));
        }
    }

    private Object dispatchMethod(String method, Object params) throws JsonProcessingException {
        return switch (method) {
            case "createNote" -> {
                CreateNoteParams noteParams = convertParams(params, CreateNoteParams.class);
                yield mcpService.createNote(noteParams);
            }
            case "updateNote" -> {
                UpdateNoteParams updateParams = convertParams(params, UpdateNoteParams.class);
                yield mcpService.updateNote(updateParams);
            }
            case "getNotes" -> {
                GetNotesParams getParams = params == null
                    ? new GetNotesParams()
                    : convertParams(params, GetNotesParams.class);
                Page<DocumentDTO> documents = mcpService.getNotes(getParams);
                yield createPageResponse(documents);
            }
            case "searchNotes" -> {
                SearchNotesParams searchParams = convertParams(params, SearchNotesParams.class);
                Page<SearchResultDTO> results = mcpService.searchNotes(searchParams);
                yield createPageResponse(results);
            }
            case "batchCreateNotes" -> {
                BatchCreateNotesParams batchParams = convertParams(params, BatchCreateNotesParams.class);
                yield mcpService.batchCreateNotes(batchParams);
            }
            default -> throw new UnsupportedOperationException("Method not supported: " + method);
        };
    }

    private <T> T convertParams(Object params, Class<T> targetClass) throws JsonProcessingException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters are required");
        }
        // Convert params object to target class
        String json = objectMapper.writeValueAsString(params);
        return objectMapper.readValue(json, targetClass);
    }

    private <T> Map<String, Object> createPageResponse(Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("number", page.getNumber());
        response.put("size", page.getSize());
        response.put("hasNext", page.hasNext());
        response.put("hasPrevious", page.hasPrevious());
        return response;
    }

    @GetMapping("/manifest")
    @Operation(summary = "Get MCP manifest", description = "Returns the manifest describing all available MCP methods and their parameters")
    public ResponseEntity<Map<String, Object>> getManifest() {
        Map<String, Object> manifest = new HashMap<>();
        manifest.put("protocol", "mcp");
        manifest.put("version", "1.0.0");
        manifest.put("name", "Tenga Knowledge Base");
        manifest.put("description", "Personal knowledge base with document management, tagging, and search");

        Map<String, Object> methods = new HashMap<>();

        methods.put("createNote", Map.of(
            "description", "Create a new knowledge document",
            "params", Map.of(
                "title", "(optional) Document title. If omitted, extracted from content.",
                "content", "(required) Document content in markdown format.",
                "tags", "(optional) List of tag names to associate with the document.",
                "suggestTags", "(optional) Boolean flag to auto-suggest tags from content."
            ),
            "returns", "DocumentDTO with id, title, content, tags, timestamps, and version"
        ));

        methods.put("updateNote", Map.of(
            "description", "Update an existing document",
            "params", Map.of(
                "documentId", "(required) ID of the document to update.",
                "title", "(optional) New title.",
                "content", "(optional) New content.",
                "tags", "(optional) New tags list.",
                "append", "(optional) Boolean flag to append content instead of replacing.",
                "changeSummary", "(optional) Description of changes for version history."
            ),
            "returns", "Updated DocumentDTO"
        ));

        methods.put("getNotes", Map.of(
            "description", "Retrieve documents with optional filtering",
            "params", Map.of(
                "documentId", "(optional) Specific document ID to retrieve.",
                "tags", "(optional) List of tags to filter by (includes child tags).",
                "page", "(optional) Page number (default: 0).",
                "size", "(optional) Page size (default: 20).",
                "includeFullContent", "(optional) Boolean flag to include full content (default: false, content truncated)."
            ),
            "returns", "Paginated list of documents"
        ));

        methods.put("searchNotes", Map.of(
            "description", "Search documents using full-text search",
            "params", Map.of(
                "query", "(required) Search query string.",
                "tags", "(optional) List of tags to filter results.",
                "page", "(optional) Page number (default: 0).",
                "size", "(optional) Page size (default: 20).",
                "semantic", "(optional) Boolean flag for semantic search (not yet implemented)."
            ),
            "returns", "Paginated search results with snippets and relevance scores"
        ));

        methods.put("batchCreateNotes", Map.of(
            "description", "Create multiple documents in a single transaction",
            "params", Map.of(
                "notes", "(required) Array of CreateNoteParams objects."
            ),
            "returns", "List of created DocumentDTOs"
        ));

        manifest.put("methods", methods);

        Map<String, String> authentication = new HashMap<>();
        authentication.put("type", "bearer");
        authentication.put("header", "Authorization");
        authentication.put("format", "Bearer <api-key>");
        manifest.put("authentication", authentication);

        return ResponseEntity.ok(manifest);
    }
}
