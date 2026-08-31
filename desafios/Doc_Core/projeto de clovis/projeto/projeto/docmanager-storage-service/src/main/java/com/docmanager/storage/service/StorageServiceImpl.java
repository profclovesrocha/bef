package com.docmanager.storage.service;

import com.docmanager.common.dto.StorageResultDTO;
import com.docmanager.common.exception.StorageException;
import com.docmanager.common.util.FileUtils;
import com.docmanager.storage.config.StorageProperties;
import com.docmanager.storage.factory.StorageStrategyFactory;
import com.docmanager.storage.strategy.StorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Implementação do serviço de armazenamento.
 * Delega operações para a {@link StorageStrategy} configurada.
 */
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final StorageStrategy activeStrategy;

    public StorageServiceImpl(StorageStrategyFactory factory, StorageProperties properties) {
        this.activeStrategy = factory.getStrategy(properties.getType());
        log.info("Estratégia de armazenamento ativa: {}", properties.getType());
    }

    @Override
    public StorageResultDTO store(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtils.getExtension(originalFilename);
            String key = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);

            String contentType = file.getContentType() != null
                    ? file.getContentType()
                    : "application/octet-stream";

            String storedKey = activeStrategy.store(key, file.getBytes(), contentType);

            return StorageResultDTO.builder()
                    .storageKey(storedKey)
                    .contentType(contentType)
                    .size(file.getSize())
                    .success(true)
                    .message("Arquivo armazenado com sucesso")
                    .build();
        } catch (Exception e) {
            log.error("Erro ao armazenar arquivo: {}", e.getMessage());
            throw new StorageException("Falha ao armazenar arquivo", e);
        }
    }

    @Override
    public byte[] retrieve(String key) {
        return activeStrategy.retrieve(key);
    }

    @Override
    public void delete(String key) {
        activeStrategy.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return activeStrategy.exists(key);
    }
}
