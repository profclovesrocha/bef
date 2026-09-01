package com.docmanager.document.client;

import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.dto.VersionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 * Cliente REST para comunicação com o Version Service.
 * Permite ao Document Service criar versões e consultar histórico.
 */
@Component
@Slf4j
public class VersionClient {

    private final RestClient restClient;

    public VersionClient(@Value("${docmanager.services.version-url:http://localhost:8084}") String versionUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(versionUrl)
                .build();
    }

    /**
     * Cria uma nova versão para um documento.
     */
    public VersionDTO createVersion(UUID documentId, String storageKey,
                                     Long fileSize, String checksum, String createdBy) {
        try {
            VersionCreateRequest request = VersionCreateRequest.builder()
                    .documentId(documentId)
                    .changeDescription("Upload de arquivo")
                    .createdBy(createdBy)
                    .build();

            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/versions")
                            .queryParam("storageKey", storageKey)
                            .queryParam("fileSize", fileSize)
                            .queryParam("checksum", checksum != null ? checksum : "")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(VersionDTO.class);
        } catch (Exception e) {
            log.warn("Erro ao criar versão no Version Service: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Busca a última versão de um documento.
     */
    public VersionDTO getLatestVersion(UUID documentId) {
        try {
            return restClient.get()
                    .uri("/api/v1/versions/document/{docId}/latest", documentId)
                    .retrieve()
                    .body(VersionDTO.class);
        } catch (Exception e) {
            log.warn("Erro ao buscar última versão: {}", e.getMessage());
            return null;
        }
    }
}
