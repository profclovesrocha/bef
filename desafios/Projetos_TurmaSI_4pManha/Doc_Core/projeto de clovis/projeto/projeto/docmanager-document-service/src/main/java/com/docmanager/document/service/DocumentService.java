package com.docmanager.document.service;

import com.docmanager.common.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Interface do serviço de documentos.
 * Define o contrato para que implementações possam ser trocadas.
 */
public interface DocumentService {

    DocumentDTO create(DocumentCreateRequest request, MultipartFile file);

    DocumentDTO findById(UUID id);

    PageResponse<DocumentDTO> findAll(Pageable pageable);

    PageResponse<DocumentDTO> search(String name, String fileType, Pageable pageable);

    DocumentDTO update(UUID id, DocumentUpdateRequest request);

    void delete(UUID id);

    byte[] downloadFile(UUID id);

    PageResponse<DocumentDTO> fullTextSearch(String query, Pageable pageable);
}
