package com.docmanager.version.repository;

import com.docmanager.version.entity.DocumentVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VersionRepository extends JpaRepository<DocumentVersion, UUID> {

    /**
     * Lista todas as versões de um documento com paginação.
     */
    Page<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(UUID documentId, Pageable pageable);

    /**
     * Busca a última versão de um documento.
     */
    Optional<DocumentVersion> findFirstByDocumentIdOrderByVersionNumberDesc(UUID documentId);

    /**
     * Busca uma versão específica por número.
     */
    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(UUID documentId, Integer versionNumber);

    /**
     * Conta versões de um documento.
     */
    long countByDocumentId(UUID documentId);
}
