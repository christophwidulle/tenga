package de.christophdick.tenga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.christophdick.tenga.dto.CreateDocumentRequest;
import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.service.ApiKeyService;
import de.christophdick.tenga.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private ApiKeyService apiKeyService;

    @Test
    @WithMockUser
    void testCreateDocument_Success() throws Exception {
        CreateDocumentRequest request = new CreateDocumentRequest();
        request.setTitle("Test Document");
        request.setContent("Test content");
        request.setTags(Set.of("test"));

        DocumentDTO response = new DocumentDTO();
        response.setId(1L);
        response.setTitle("Test Document");
        response.setContent("Test content");
        response.setTags(new HashSet<>());
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        response.setCurrentVersion(1);

        when(documentService.createDocument(any(CreateDocumentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/documents")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Document"));
    }

    @Test
    @WithMockUser
    void testGetDocument_Success() throws Exception {
        DocumentDTO response = new DocumentDTO();
        response.setId(1L);
        response.setTitle("Test Document");
        response.setContent("Test content");
        response.setTags(new HashSet<>());
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        response.setCurrentVersion(1);

        when(documentService.getDocumentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/documents/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Document"));
    }

    @Test
    @WithMockUser
    void testDeleteDocument_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/documents/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
