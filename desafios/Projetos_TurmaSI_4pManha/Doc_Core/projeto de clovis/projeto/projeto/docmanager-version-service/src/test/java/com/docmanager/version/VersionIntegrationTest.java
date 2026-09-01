package com.docmanager.version;

import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.dto.VersionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do Version Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Version Service - Testes de Integração")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VersionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID DOC_ID = UUID.randomUUID();
    private static UUID firstVersionId;

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/versions - Criar primeira versão")
    void shouldCreateFirstVersion() throws Exception {
        VersionCreateRequest request = VersionCreateRequest.builder()
                .documentId(DOC_ID)
                .changeDescription("Versão inicial do documento")
                .createdBy("test-user")
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("storageKey", "test-key-v1.pdf")
                        .param("fileSize", "1024")
                        .param("checksum", "abc123hash"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.versionNumber").value(1))
                .andExpect(jsonPath("$.changeDescription").value("Versão inicial do documento"))
                .andReturn();

        VersionDTO dto = objectMapper.readValue(
                result.getResponse().getContentAsString(), VersionDTO.class);
        firstVersionId = dto.getId();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/v1/versions - Criar segunda versão (auto-incremento)")
    void shouldCreateSecondVersion() throws Exception {
        VersionCreateRequest request = VersionCreateRequest.builder()
                .documentId(DOC_ID)
                .changeDescription("Correções de texto")
                .createdBy("test-user")
                .build();

        mockMvc.perform(post("/api/v1/versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("storageKey", "test-key-v2.pdf")
                        .param("fileSize", "2048"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.versionNumber").value(2));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/v1/versions/document/{docId} - Listar versões do documento")
    void shouldListVersionsByDocument() throws Exception {
        mockMvc.perform(get("/api/v1/versions/document/{docId}", DOC_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].versionNumber").value(2))
                .andExpect(jsonPath("$.content[1].versionNumber").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/v1/versions/document/{docId}/latest - Última versão")
    void shouldGetLatestVersion() throws Exception {
        mockMvc.perform(get("/api/v1/versions/document/{docId}/latest", DOC_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versionNumber").value(2));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/v1/versions/{id}/restore - Restaurar versão 1")
    void shouldRestoreVersion() throws Exception {
        Assumptions.assumeTrue(firstVersionId != null);

        mockMvc.perform(post("/api/v1/versions/{id}/restore", firstVersionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versionNumber").value(3))
                .andExpect(jsonPath("$.changeDescription").value("Restaurado a partir da versão 1"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/v1/versions/document/{docId}/history - Histórico completo")
    void shouldGetHistory() throws Exception {
        mockMvc.perform(get("/api/v1/versions/document/{docId}/history", DOC_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(3)); // 2 criações + 1 restauração
    }
}
