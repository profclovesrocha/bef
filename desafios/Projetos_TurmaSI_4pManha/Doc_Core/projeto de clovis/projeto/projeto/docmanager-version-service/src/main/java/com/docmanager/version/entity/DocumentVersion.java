package com.docmanager.version.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa uma versão específica de um documento.
 * Cada atualização de arquivo cria uma nova versão, permitindo
 * rastrear o histórico completo.
 */
@Entity
@Table(name = "document_versions", indexes = {
        @Index(name = "idx_ver_doc_id", columnList = "documentId"),
        @Index(name = "idx_ver_number", columnList = "documentId, versionNumber"),
        @Index(name = "idx_ver_created", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID documentId;

    @Column(nullable = false)
    private Integer versionNumber;

    private String storageKey;

    @Column(length = 500)
    private String changeDescription;

    private Long fileSize;

    /**
     * Hash SHA-256 do conteúdo para verificação de integridade.
     */
    private String checksum;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
