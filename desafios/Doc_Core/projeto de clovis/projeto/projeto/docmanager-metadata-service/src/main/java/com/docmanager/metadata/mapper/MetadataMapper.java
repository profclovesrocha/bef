package com.docmanager.metadata.mapper;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataEntryDTO;
import com.docmanager.common.dto.MetadataRequest;
import com.docmanager.metadata.entity.Metadata;
import com.docmanager.metadata.entity.MetadataEntry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MetadataMapper {

    public MetadataDTO toDTO(Metadata entity) {
        if (entity == null) return null;

        return MetadataDTO.builder()
                .id(entity.getId())
                .documentId(entity.getDocumentId())
                .category(entity.getCategory())
                .tags(entity.getTags())
                .customProperties(entity.getCustomProperties() != null
                        ? entity.getCustomProperties().stream()
                        .map(this::toEntryDTO)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Metadata toEntity(MetadataRequest request) {
        if (request == null) return null;

        return Metadata.builder()
                .documentId(request.getDocumentId())
                .category(request.getCategory())
                .tags(request.getTags() != null ? request.getTags() : new HashSet<>())
                .customProperties(request.getCustomProperties() != null
                        ? request.getCustomProperties().stream()
                        .map(this::toEntryEntity)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    private MetadataEntryDTO toEntryDTO(MetadataEntry entry) {
        return MetadataEntryDTO.builder()
                .key(entry.getKey())
                .value(entry.getValue())
                .build();
    }

    private MetadataEntry toEntryEntity(MetadataEntryDTO dto) {
        return MetadataEntry.builder()
                .key(dto.getKey())
                .value(dto.getValue())
                .build();
    }
}
