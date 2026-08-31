package com.docmanager.document;

import com.docmanager.common.dto.DocumentCreateRequest;
import com.docmanager.common.dto.DocumentDTO;
import com.docmanager.common.dto.DocumentUpdateRequest;
import com.docmanager.document.client.StorageClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do Document Service.
 * Usa banco H2 em memória (profile dev) e mock do StorageClient.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Document Service - Testes de Integração")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StorageClient storageClient;

    private static UUID createdDocId;

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/documents - Criar documento via multipart sem arquivo")
    void shouldCreateDocumentWithoutFile() throws Exception {
        DocumentCreateRequest request = DocumentCreateRequest.builder()
                .name("Contrato de Prestação de Serviços")
                .description("Contrato firmado em agosto de 2024")
                .createdBy("integração-teste")
                .build();

        // Envia o JSON do request como um MockMultipartFile (part "document")
        MockMultipartFile documentPart = new MockMultipartFile(
                "document", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        MvcResult result = mockMvc.perform(multipart("/api/v1/documents")
                        .file(documentPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Contrato de Prestação de Serviços"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        DocumentDTO doc = objectMapper.readValue(json, DocumentDTO.class);
        createdDocId = doc.getId();
        assertNotNull(createdDocId);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/v1/documents - Listar documentos paginados")
    void shouldListDocumentsPaginated() throws Exception {
        mockMvc.perform(get("/api/v1/documents")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/v1/documents/{id} - Buscar documento existente")
    void shouldFindDocumentById() throws Exception {
        Assumptions.assumeTrue(createdDocId != null, "Documento precisa ter sido criado antes");

        mockMvc.perform(get("/api/v1/documents/{id}", createdDocId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdDocId.toString()))
                .andExpect(jsonPath("$.name").value("Contrato de Prestação de Serviços"));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/v1/documents/{id} - 404 para ID inexistente")
    void shouldReturn404ForNonExistentDocument() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/documents/{id}", fakeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Documento Não Encontrado"));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /api/v1/documents/{id} - Atualizar documento")
    void shouldUpdateDocument() throws Exception {
        Assumptions.assumeTrue(createdDocId != null);

        DocumentUpdateRequest updateRequest = DocumentUpdateRequest.builder()
                .name("Contrato Atualizado v2")
                .description("Descrição atualizada")
                .build();

        mockMvc.perform(put("/api/v1/documents/{id}", createdDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Contrato Atualizado v2"))
                .andExpect(jsonPath("$.description").value("Descrição atualizada"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/v1/documents/search - Buscar por nome")
    void shouldSearchByName() throws Exception {
        mockMvc.perform(get("/api/v1/documents/search")
                        .param("name", "Contrato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /api/v1/documents/{id} - Soft delete")
    void shouldSoftDeleteDocument() throws Exception {
        Assumptions.assumeTrue(createdDocId != null);

        mockMvc.perform(delete("/api/v1/documents/{id}", createdDocId))
                .andExpect(status().isNoContent());

        // Confirma que não aparece mais nas buscas
        mockMvc.perform(get("/api/v1/documents/{id}", createdDocId))
                .andExpect(status().isNotFound());
    }
}
