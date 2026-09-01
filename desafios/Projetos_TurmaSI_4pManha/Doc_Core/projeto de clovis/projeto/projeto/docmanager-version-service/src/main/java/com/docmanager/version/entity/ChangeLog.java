package com.docmanager.version.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de alteração no histórico de um documento.
 * Captura quem fez a alteração, quando e o que mudou.
 */
@Entity
@Table(name = "change_logs", indexes = {
        @Index(name = "idx_log_doc_id", columnList = "documentId"),
        @Index(name = "idx_log_created", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID documentId;

    @Column(nullable = false)
    private String action; // CREATED, UPDATED, VERSION_CREATED, RESTORED, DELETED

    @Column(length = 1000)
    private String description;

    private Integer versionNumber;

    private String performedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
