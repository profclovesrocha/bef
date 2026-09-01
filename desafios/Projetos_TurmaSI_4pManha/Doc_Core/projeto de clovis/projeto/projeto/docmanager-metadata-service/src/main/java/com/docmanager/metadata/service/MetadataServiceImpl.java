package com.docmanager.metadata.service;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataRequest;
import com.docmanager.common.dto.PageResponse;
import com.docmanager.common.exception.MetadataNotFoundException;
import com.docmanager.metadata.entity.Metadata;
import com.docmanager.metadata.entity.MetadataEntry;
import com.docmanager.metadata.mapper.MetadataMapper;
import com.docmanager.metadata.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MetadataServiceImpl implements MetadataService {

    private final MetadataRepository metadataRepository;
    private final MetadataMapper metadataMapper;

    @Override
    public MetadataDTO create(MetadataRequest request) {
        log.info("Criando metadados para documento: {}", request.getDocumentId());

        Metadata metadata = metadataMapper.toEntity(request);
        Metadata saved = metadataRepository.save(metadata);

        return metadataMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetadataDTO> findByDocumentId(UUID documentId) {
        return metadataRepository.findByDocumentId(documentId).stream()
                .map(metadataMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MetadataDTO findById(UUID id) {
        Metadata metadata = findOrThrow(id);
        return metadataMapper.toDTO(metadata);
    }

    @Override
    public MetadataDTO update(UUID id, MetadataRequest request) {
        log.info("Atualizando metadados: {}", id);

        Metadata metadata = findOrThrow(id);

        if (request.getCategory() != null) {
            metadata.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            metadata.getTags().clear();
            metadata.getTags().addAll(request.getTags());
        }
        if (request.getCustomProperties() != null) {
            metadata.getCustomProperties().clear();
            request.getCustomProperties().forEach(dto -> {
                MetadataEntry entry = MetadataEntry.builder()
                        .key(dto.getKey())
                        .value(dto.getValue())
                        .build();
                metadata.getCustomProperties().add(entry);
            });
        }

        Metadata updated = metadataRepository.save(metadata);
        return metadataMapper.toDTO(updated);
    }

    @Override
    public void delete(UUID id) {
        log.info("Excluindo metadados: {}", id);
        findOrThrow(id);
        metadataRepository.deleteById(id);
    }

    @Override
    public void deleteByDocumentId(UUID documentId) {
        log.info("Excluindo todos os metadados do documento: {}", documentId);
        metadataRepository.deleteByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MetadataDTO> search(String category, String tag, Pageable pageable) {
        Page<Metadata> page = metadataRepository.search(category, tag, pageable);

        return PageResponse.<MetadataDTO>builder()
                .content(page.getContent().stream().map(metadataMapper::toDTO).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private Metadata findOrThrow(UUID id) {
        return metadataRepository.findById(id)
                .orElseThrow(() -> new MetadataNotFoundException(id));
    }
}
