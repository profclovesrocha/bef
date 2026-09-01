package com.docmanager.document.service;

import com.docmanager.common.dto.*;
import com.docmanager.common.exception.DocumentNotFoundException;
import com.docmanager.common.exception.InvalidDocumentException;
import com.docmanager.common.util.FileUtils;
import com.docmanager.document.client.StorageClient;
import com.docmanager.document.entity.Document;
import com.docmanager.document.mapper.DocumentMapper;
import com.docmanager.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Implementação do serviço de documentos.
 * Orquestra operações CRUD com integração ao Storage Service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final StorageClient storageClient;

    @Override
    public DocumentDTO create(DocumentCreateRequest request, MultipartFile file) {
        log.info("Criando documento: {}", request.getName());

        Document document = documentMapper.toEntity(request);

        // Se um arquivo foi enviado, armazena via Storage Service
        if (file != null && !file.isEmpty()) {
            validateFile(file);

            StorageResultDTO storageResult = storageClient.upload(file);
            document.setStorageKey(storageResult.getStorageKey());
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
        }

        Document saved = documentRepository.save(document);
        log.info("Documento criado com ID: {}", saved.getId());

        return documentMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDTO findById(UUID id) {
        Document document = findDocumentOrThrow(id);
        return documentMapper.toDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentDTO> findAll(Pageable pageable) {
        Page<Document> page = documentRepository.findByDeletedFalse(pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentDTO> search(String name, String fileType, Pageable pageable) {
        Page<Document> page = documentRepository.search(name, fileType, pageable);
        return toPageResponse(page);
    }

    @Override
    public DocumentDTO update(UUID id, DocumentUpdateRequest request) {
        log.info("Atualizando documento: {}", id);

        Document document = findDocumentOrThrow(id);

        if (request.getName() != null) {
            document.setName(request.getName());
        }
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }

        Document updated = documentRepository.save(document);
        log.info("Documento atualizado: {}", id);

        return documentMapper.toDTO(updated);
    }

    @Override
    public void delete(UUID id) {
        log.info("Excluindo documento (soft delete): {}", id);

        Document document = findDocumentOrThrow(id);
        document.setDeleted(true);
        documentRepository.save(document);

        // Remove arquivo do storage se existir
        if (document.getStorageKey() != null) {
            try {
                storageClient.delete(document.getStorageKey());
            } catch (Exception e) {
                log.warn("Erro ao remover arquivo do storage: {}", e.getMessage());
            }
        }

        log.info("Documento excluído: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadFile(UUID id) {
        Document document = findDocumentOrThrow(id);

        if (document.getStorageKey() == null) {
            throw new InvalidDocumentException("Este documento não possui arquivo associado");
        }

        return storageClient.download(document.getStorageKey());
    }

    // --- Métodos privados auxiliares ---

    private Document findDocumentOrThrow(UUID id) {
        return documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    private void validateFile(MultipartFile file) {
        if (!FileUtils.isWithinSizeLimit(file.getSize())) {
            throw new InvalidDocumentException(
                    "Arquivo excede o tamanho máximo de " + (FileUtils.getMaxFileSize() / 1024 / 1024) + " MB");
        }

        String filename = file.getOriginalFilename();
        if (filename != null && !FileUtils.isAllowedExtension(filename)) {
            throw new InvalidDocumentException(
                    "Tipo de arquivo não permitido: " + FileUtils.getExtension(filename));
        }
    }

    private PageResponse<DocumentDTO> toPageResponse(Page<Document> page) {
        return PageResponse.<DocumentDTO>builder()
                .content(page.getContent().stream().map(documentMapper::toDTO).toList())
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
    public PageResponse<DocumentDTO> fullTextSearch(String query, Pageable pageable) {
        log.info("Busca full-text: '{}'", query);
        Page<Document> page = documentRepository.fullTextSearch(query, pageable);
        return toPageResponse(page);
    }
}
