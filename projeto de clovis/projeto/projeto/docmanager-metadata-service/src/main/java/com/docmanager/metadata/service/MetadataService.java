package com.docmanager.metadata.service;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataRequest;
import com.docmanager.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MetadataService {

    MetadataDTO create(MetadataRequest request);

    List<MetadataDTO> findByDocumentId(UUID documentId);

    MetadataDTO findById(UUID id);

    MetadataDTO update(UUID id, MetadataRequest request);

    void delete(UUID id);

    void deleteByDocumentId(UUID documentId);

    PageResponse<MetadataDTO> search(String category, String tag, Pageable pageable);
}
