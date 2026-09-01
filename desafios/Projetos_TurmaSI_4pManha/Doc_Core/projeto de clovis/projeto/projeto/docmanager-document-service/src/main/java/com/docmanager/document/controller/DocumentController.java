package com.docmanager.document.controller;

import com.docmanager.common.dto.*;
import com.docmanager.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller REST para gerenciamento de documentos.
 * Expõe endpoints CRUD com paginação, filtros e upload de arquivos.
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "API para gerenciamento de documentos")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar documento", description = "Cria um novo documento com upload opcional de arquivo")
    public ResponseEntity<DocumentDTO> create(
            @Valid @RequestPart("document") DocumentCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        DocumentDTO created = documentService.create(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar documentos", description = "Lista todos os documentos com paginação")
    public ResponseEntity<PageResponse<DocumentDTO>> findAll(
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(documentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar documento", description = "Busca um documento por ID")
    public ResponseEntity<DocumentDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar documentos", description = "Busca documentos por nome e/ou tipo de arquivo")
    public ResponseEntity<PageResponse<DocumentDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fileType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(documentService.search(name, fileType, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar documento", description = "Atualiza nome e/ou descrição de um documento")
    public ResponseEntity<DocumentDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody DocumentUpdateRequest request) {

        return ResponseEntity.ok(documentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir documento", description = "Exclui um documento (soft delete)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        documentService.delete(id);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download de arquivo", description = "Faz download do arquivo associado ao documento")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        DocumentDTO doc = documentService.findById(id);
        byte[] fileData = documentService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        doc.getFileType() != null ? doc.getFileType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getName() + "\"")
                .body(fileData);
    }

    @GetMapping("/fulltext-search")
    @Operation(summary = "Busca full-text",
            description = "Pesquisa em nome, descrição, tipo e autor do documento")
    public ResponseEntity<PageResponse<DocumentDTO>> fullTextSearch(
            @Parameter(description = "Termo de busca")
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(documentService.fullTextSearch(query, pageable));
    }
}
