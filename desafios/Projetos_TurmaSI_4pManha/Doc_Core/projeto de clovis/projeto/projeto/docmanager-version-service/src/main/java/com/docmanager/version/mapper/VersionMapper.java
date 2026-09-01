package com.docmanager.version.mapper;

import com.docmanager.common.dto.VersionDTO;
import com.docmanager.version.entity.DocumentVersion;
import org.springframework.stereotype.Component;

@Component
public class VersionMapper {

    public VersionDTO toDTO(DocumentVersion entity) {
        if (entity == null) return null;

        return VersionDTO.builder()
                .id(entity.getId())
                .documentId(entity.getDocumentId())
                .versionNumber(entity.getVersionNumber())
                .storageKey(entity.getStorageKey())
                .changeDescription(entity.getChangeDescription())
                .fileSize(entity.getFileSize())
                .checksum(entity.getChecksum())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
