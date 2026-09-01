package com.docmanager.version.service;

import com.docmanager.common.dto.PageResponse;
import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.dto.VersionDTO;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VersionService {

    VersionDTO createVersion(VersionCreateRequest request, String storageKey, Long fileSize, String checksum);

    VersionDTO findById(UUID id);

    PageResponse<VersionDTO> findByDocumentId(UUID documentId, Pageable pageable);

    VersionDTO findLatestVersion(UUID documentId);

    VersionDTO restoreVersion(UUID versionId);

    PageResponse<Object> getHistory(UUID documentId, Pageable pageable);
}
