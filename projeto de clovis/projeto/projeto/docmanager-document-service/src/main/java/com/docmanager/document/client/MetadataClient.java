package com.docmanager.document.client;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Cliente REST para comunicação com o Metadata Service.
 */
@Component
@Slf4j
public class MetadataClient {

    private final RestClient restClient;

    public MetadataClient(@Value("${docmanager.services.metadata-url:http://localhost:8083}") String metadataUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(metadataUrl)
                .build();
    }

    /**
     * Cria metadados para um documento.
     */
    public MetadataDTO create(MetadataRequest request) {
        try {
            return restClient.post()
                    .uri("/api/v1/metadata")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MetadataDTO.class);
        } catch (Exception e) {
            log.warn("Erro ao criar metadados no Metadata Service: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Busca metadados de um documento.
     */
    public List<MetadataDTO> findByDocumentId(UUID documentId) {
        try {
            return restClient.get()
                    .uri("/api/v1/metadata/document/{docId}", documentId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.warn("Erro ao buscar metadados: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Exclui metadados de um documento.
     */
    public void deleteByDocumentId(UUID documentId) {
        try {
            List<MetadataDTO> metadataList = findByDocumentId(documentId);
            for (MetadataDTO metadata : metadataList) {
                restClient.delete()
                        .uri("/api/v1/metadata/{id}", metadata.getId())
                        .retrieve()
                        .toBodilessEntity();
            }
        } catch (Exception e) {
            log.warn("Erro ao remover metadados: {}", e.getMessage());
        }
    }
}
