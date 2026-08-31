package com.docmanager.document.controller;

import com.docmanager.common.dto.*;
import com.docmanager.common.exception.DocumentNotFoundException;
import com.docmanager.common.exception.GlobalExceptionHandler;
import com.docmanager.document.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("DocumentController Tests")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentService documentService;

    @Test
    @DisplayName("GET /api/v1/documents - deve retornar lista paginada")
    void shouldReturnPaginatedDocuments() throws Exception {
        DocumentDTO doc = DocumentDTO.builder()
                .id(UUID.randomUUID())
                .name("Teste")
                .createdAt(LocalDateTime.now())
                .build();

        PageResponse<DocumentDTO> response = PageResponse.<DocumentDTO>builder()
                .content(List.of(doc))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(documentService.findAll(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/documents")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Teste"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/documents/{id} - deve retornar documento")
    void shouldReturnDocumentById() throws Exception {
        UUID id = UUID.randomUUID();
        DocumentDTO doc = DocumentDTO.builder()
                .id(id)
                .name("Meu Doc")
                .createdAt(LocalDateTime.now())
                .build();

        when(documentService.findById(id)).thenReturn(doc);

        mockMvc.perform(get("/api/v1/documents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Meu Doc"));
    }

    @Test
    @DisplayName("GET /api/v1/documents/{id} - deve retornar 404")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentService.findById(id)).thenThrow(new DocumentNotFoundException(id));

        mockMvc.perform(get("/api/v1/documents/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Documento Não Encontrado"));
    }

    @Test
    @DisplayName("PUT /api/v1/documents/{id} - deve atualizar documento")
    void shouldUpdateDocument() throws Exception {
        UUID id = UUID.randomUUID();
        DocumentUpdateRequest request = DocumentUpdateRequest.builder()
                .name("Nome Atualizado")
                .build();

        DocumentDTO updated = DocumentDTO.builder()
                .id(id)
                .name("Nome Atualizado")
                .createdAt(LocalDateTime.now())
                .build();

        when(documentService.update(eq(id), any(DocumentUpdateRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/documents/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/documents/{id} - deve retornar 204")
    void shouldDeleteDocument() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/documents/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/documents/search - deve buscar por nome")
    void shouldSearchByName() throws Exception {
        PageResponse<DocumentDTO> response = PageResponse.<DocumentDTO>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();

        when(documentService.search(eq("relatorio"), any(), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/documents/search")
                        .param("name", "relatorio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
