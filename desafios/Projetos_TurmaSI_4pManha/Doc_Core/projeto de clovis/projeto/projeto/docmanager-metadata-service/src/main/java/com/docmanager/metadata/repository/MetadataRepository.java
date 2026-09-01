package com.docmanager.metadata.repository;

import com.docmanager.metadata.entity.Metadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, UUID> {

    /**
     * Busca metadados por ID do documento.
     */
    List<Metadata> findByDocumentId(UUID documentId);

    /**
     * Busca metadados por categoria.
     */
    Page<Metadata> findByCategory(String category, Pageable pageable);

    /**
     * Busca metadados que contêm uma tag específica.
     */
    @Query("SELECT m FROM Metadata m JOIN m.tags t WHERE t = :tag")
    Page<Metadata> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * Busca por categoria e/ou tag.
     */
    @Query("SELECT DISTINCT m FROM Metadata m LEFT JOIN m.tags t " +
            "WHERE (:category IS NULL OR m.category = :category) " +
            "AND (:tag IS NULL OR t = :tag)")
    Page<Metadata> search(@Param("category") String category,
                          @Param("tag") String tag,
                          Pageable pageable);

    /**
     * Remove todos os metadados de um documento.
     */
    void deleteByDocumentId(UUID documentId);
}
