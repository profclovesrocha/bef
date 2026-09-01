package com.docmanager.document.client;

import com.docmanager.common.dto.StorageResultDTO;
import com.docmanager.common.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

/**
 * Cliente REST para comunicação com o Storage Service.
 * Encapsula as chamadas HTTP entre microsserviços.
 */
@Component
@Slf4j
public class StorageClient {

    private final RestClient restClient;

    public StorageClient(@Value("${docmanager.services.storage-url:http://localhost:8082}") String storageUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(storageUrl)
                .build();
    }

    /**
     * Faz upload de arquivo para o Storage Service.
     */
    public StorageResultDTO upload(MultipartFile file) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            }).contentType(MediaType.parseMediaType(
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream"));

            return restClient.post()
                    .uri("/api/v1/storage/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build())
                    .retrieve()
                    .body(StorageResultDTO.class);
        } catch (Exception e) {
            log.error("Erro ao enviar arquivo para Storage Service: {}", e.getMessage());
            throw new StorageException("Falha ao armazenar arquivo", e);
        }
    }

    /**
     * Faz download de arquivo do Storage Service.
     */
    public byte[] download(String storageKey) {
        try {
            return restClient.get()
                    .uri("/api/v1/storage/{key}", storageKey)
                    .retrieve()
                    .body(byte[].class);
        } catch (Exception e) {
            log.error("Erro ao baixar arquivo do Storage Service: {}", e.getMessage());
            throw new StorageException("Falha ao recuperar arquivo", e);
        }
    }

    /**
     * Remove arquivo do Storage Service.
     */
    public void delete(String storageKey) {
        try {
            restClient.delete()
                    .uri("/api/v1/storage/{key}", storageKey)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Erro ao remover arquivo do Storage Service: {}", e.getMessage());
        }
    }
}
