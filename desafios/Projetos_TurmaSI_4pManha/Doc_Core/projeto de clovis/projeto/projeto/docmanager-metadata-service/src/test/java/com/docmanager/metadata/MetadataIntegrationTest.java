package com.docmanager.metadata;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataEntryDTO;
import com.docmanager.common.dto.MetadataRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do Metadata Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Metadata Service - Testes de Integração")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MetadataIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static UUID createdMetadataId;
    private static final UUID DOC_ID = UUID.randomUUID();

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/metadata - Criar metadados com tags e propriedades")
    void shouldCreateMetadata() throws Exception {
        MetadataRequest request = MetadataRequest.builder()
                .documentId(DOC_ID)
                .category("Financeiro")
                .tags(Set.of("anual", "receita", "2024"))
                .customProperties(List.of(
                        MetadataEntryDTO.builder().key("autor").value("João Silva").build(),
                        MetadataEntryDTO.builder().key("departamento").value("Contabilidade").build()
                ))
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Financeiro"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.customProperties").isArray())
                .andReturn();

        MetadataDTO dto = objectMapper.readValue(
                result.getResponse().getContentAsString(), MetadataDTO.class);
        createdMetadataId = dto.getId();
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/v1/metadata/document/{docId} - Buscar por documento")
    void shouldFindByDocumentId() throws Exception {
        mockMvc.perform(get("/api/v1/metadata/document/{docId}", DOC_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("Financeiro"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/v1/metadata/search - Buscar por tag")
    void shouldSearchByTag() throws Exception {
        mockMvc.perform(get("/api/v1/metadata/search")
                        .param("tag", "anual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/v1/metadata/{id} - Atualizar categoria")
    void shouldUpdateMetadata() throws Exception {
        Assumptions.assumeTrue(createdMetadataId != null);

        MetadataRequest updateRequest = MetadataRequest.builder()
                .documentId(DOC_ID)
                .category("RH")
                .tags(Set.of("pessoal", "2024"))
                .build();

        mockMvc.perform(put("/api/v1/metadata/{id}", createdMetadataId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("RH"));
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/v1/metadata/{id} - Excluir")
    void shouldDeleteMetadata() throws Exception {
        Assumptions.assumeTrue(createdMetadataId != null);

        mockMvc.perform(delete("/api/v1/metadata/{id}", createdMetadataId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/metadata/{id}", createdMetadataId))
                .andExpect(status().isNotFound());
    }
}
