package com.docmanager.document.repository;

import com.docmanager.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a entidade Document.
 * Todos os métodos filtram documentos não-deletados por padrão.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    /**
     * Busca documento por ID, excluindo deletados.
     */
    Optional<Document> findByIdAndDeletedFalse(UUID id);

    /**
     * Lista todos os documentos não-deletados com paginação.
     */
    Page<Document> findByDeletedFalse(Pageable pageable);

    /**
     * Busca documentos por nome (case insensitive, parcial).
     */
    @Query("SELECT d FROM Document d WHERE d.deleted = false AND LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Document> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Busca documentos por tipo de arquivo.
     */
    Page<Document> findByFileTypeAndDeletedFalse(String fileType, Pageable pageable);

    /**
     * Busca por nome e/ou tipo.
     */
    @Query("SELECT d FROM Document d WHERE d.deleted = false " +
            "AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:fileType IS NULL OR d.fileType = :fileType)")
    Page<Document> search(@Param("name") String name,
                          @Param("fileType") String fileType,
                          Pageable pageable);

    /**
     * Busca full-text: pesquisa em nome, descrição, tipo e autor.
     */
    @Query("SELECT d FROM Document d WHERE d.deleted = false " +
            "AND (LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.fileType) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.createdBy) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> fullTextSearch(@Param("query") String query, Pageable pageable);
}
