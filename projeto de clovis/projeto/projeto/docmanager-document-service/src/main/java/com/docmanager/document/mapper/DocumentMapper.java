package com.docmanager.document.mapper;

import com.docmanager.common.dto.DocumentDTO;
import com.docmanager.common.dto.DocumentCreateRequest;
import com.docmanager.document.entity.Document;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Entity e DTO.
 * Centraliza a lógica de mapeamento em um único local.
 */
@Component
public class DocumentMapper {

    /**
     * Converte entidade para DTO.
     */
    public DocumentDTO toDTO(Document entity) {
        if (entity == null) return null;

        return DocumentDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .storageKey(entity.getStorageKey())
                .currentVersion(entity.getCurrentVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    /**
     * Converte request de criação para entidade.
     */
    public Document toEntity(DocumentCreateRequest request) {
        if (request == null) return null;

        return Document.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .currentVersion(1)
                .deleted(false)
                .build();
    }
}
