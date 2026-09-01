package com.docmanager.document.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA que representa um documento no sistema.
 * Usa soft delete (campo {@code deleted}) em vez de remoção física.
 */
@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_doc_name", columnList = "name"),
        @Index(name = "idx_doc_type", columnList = "fileType"),
        @Index(name = "idx_doc_created", columnList = "createdAt"),
        @Index(name = "idx_doc_deleted", columnList = "deleted")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String fileType;

    private Long fileSize;

    private String storageKey;

    @Builder.Default
    private Integer currentVersion = 1;

    @Builder.Default
    private boolean deleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
