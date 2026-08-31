package com.docmanager.version.repository;

import com.docmanager.version.entity.ChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, UUID> {

    /**
     * Lista histórico de alterações de um documento.
     */
    Page<ChangeLog> findByDocumentIdOrderByCreatedAtDesc(UUID documentId, Pageable pageable);
}
