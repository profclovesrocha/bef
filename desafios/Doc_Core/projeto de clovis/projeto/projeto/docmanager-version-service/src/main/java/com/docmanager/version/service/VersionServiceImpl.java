package com.docmanager.version.service;

import com.docmanager.common.dto.PageResponse;
import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.dto.VersionDTO;
import com.docmanager.common.exception.VersionNotFoundException;
import com.docmanager.version.entity.ChangeLog;
import com.docmanager.version.entity.DocumentVersion;
import com.docmanager.version.mapper.VersionMapper;
import com.docmanager.version.repository.ChangeLogRepository;
import com.docmanager.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VersionServiceImpl implements VersionService {

    private final VersionRepository versionRepository;
    private final ChangeLogRepository changeLogRepository;
    private final VersionMapper versionMapper;

    @Override
    public VersionDTO createVersion(VersionCreateRequest request, String storageKey,
                                     Long fileSize, String checksum) {
        log.info("Criando nova versão para documento: {}", request.getDocumentId());

        // Determina o próximo número de versão
        int nextVersion = (int) versionRepository.countByDocumentId(request.getDocumentId()) + 1;

        DocumentVersion version = DocumentVersion.builder()
                .documentId(request.getDocumentId())
                .versionNumber(nextVersion)
                .storageKey(storageKey)
                .changeDescription(request.getChangeDescription())
                .fileSize(fileSize)
                .checksum(checksum)
                .createdBy(request.getCreatedBy())
                .build();

        DocumentVersion saved = versionRepository.save(version);

        // Registra no histórico
        logChange(request.getDocumentId(), "VERSION_CREATED",
                "Versão " + nextVersion + " criada" +
                        (request.getChangeDescription() != null ? ": " + request.getChangeDescription() : ""),
                nextVersion, request.getCreatedBy());

        log.info("Versão {} criada para documento {}", nextVersion, request.getDocumentId());
        return versionMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VersionDTO findById(UUID id) {
        DocumentVersion version = versionRepository.findById(id)
                .orElseThrow(() -> new VersionNotFoundException(id));
        return versionMapper.toDTO(version);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VersionDTO> findByDocumentId(UUID documentId, Pageable pageable) {
        Page<DocumentVersion> page = versionRepository
                .findByDocumentIdOrderByVersionNumberDesc(documentId, pageable);

        return PageResponse.<VersionDTO>builder()
                .content(page.getContent().stream().map(versionMapper::toDTO).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VersionDTO findLatestVersion(UUID documentId) {
        DocumentVersion version = versionRepository
                .findFirstByDocumentIdOrderByVersionNumberDesc(documentId)
                .orElseThrow(() -> new VersionNotFoundException(
                        "Nenhuma versão encontrada para o documento: " + documentId));
        return versionMapper.toDTO(version);
    }

    @Override
    public VersionDTO restoreVersion(UUID versionId) {
        DocumentVersion oldVersion = versionRepository.findById(versionId)
                .orElseThrow(() -> new VersionNotFoundException(versionId));

        log.info("Restaurando versão {} do documento {}",
                oldVersion.getVersionNumber(), oldVersion.getDocumentId());

        // Cria uma nova versão com os dados da versão anterior
        int nextVersion = (int) versionRepository.countByDocumentId(oldVersion.getDocumentId()) + 1;

        DocumentVersion restoredVersion = DocumentVersion.builder()
                .documentId(oldVersion.getDocumentId())
                .versionNumber(nextVersion)
                .storageKey(oldVersion.getStorageKey())
                .changeDescription("Restaurado a partir da versão " + oldVersion.getVersionNumber())
                .fileSize(oldVersion.getFileSize())
                .checksum(oldVersion.getChecksum())
                .createdBy(oldVersion.getCreatedBy())
                .build();

        DocumentVersion saved = versionRepository.save(restoredVersion);

        // Registra no histórico
        logChange(oldVersion.getDocumentId(), "RESTORED",
                "Restaurado da versão " + oldVersion.getVersionNumber() + " para versão " + nextVersion,
                nextVersion, oldVersion.getCreatedBy());

        return versionMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Object> getHistory(UUID documentId, Pageable pageable) {
        Page<ChangeLog> page = changeLogRepository
                .findByDocumentIdOrderByCreatedAtDesc(documentId, pageable);

        return PageResponse.<Object>builder()
                .content(page.getContent().stream().map(log -> (Object) log).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // --- Métodos auxiliares ---

    private void logChange(UUID documentId, String action, String description,
                           Integer versionNumber, String performedBy) {
        ChangeLog changeLog = ChangeLog.builder()
                .documentId(documentId)
                .action(action)
                .description(description)
                .versionNumber(versionNumber)
                .performedBy(performedBy)
                .build();
        changeLogRepository.save(changeLog);
    }
}
