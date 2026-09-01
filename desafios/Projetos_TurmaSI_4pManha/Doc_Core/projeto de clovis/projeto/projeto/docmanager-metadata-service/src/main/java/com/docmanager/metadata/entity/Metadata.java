package com.docmanager.metadata.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entidade que armazena metadados de um documento.
 * Suporta categorias, tags e propriedades customizadas (chave-valor).
 */
@Entity
@Table(name = "metadata", indexes = {
        @Index(name = "idx_meta_doc_id", columnList = "documentId"),
        @Index(name = "idx_meta_category", columnList = "category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID documentId;

    @Column(length = 100)
    private String category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "metadata_tags",
            joinColumns = @JoinColumn(name = "metadata_id"),
            indexes = @Index(name = "idx_meta_tag", columnList = "tags"))
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    @Builder.Default
    private List<MetadataEntry> customProperties = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
