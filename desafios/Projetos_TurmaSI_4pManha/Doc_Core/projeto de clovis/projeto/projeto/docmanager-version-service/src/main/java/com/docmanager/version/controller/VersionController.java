package com.docmanager.version.controller;

import com.docmanager.common.dto.PageResponse;
import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.dto.VersionDTO;
import com.docmanager.version.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/versions")
@RequiredArgsConstructor
@Tag(name = "Versionamento", description = "API para controle de versões e histórico")
public class VersionController {

    private final VersionService versionService;

    @PostMapping
    @Operation(summary = "Criar versão", description = "Cria uma nova versão de documento")
    public ResponseEntity<VersionDTO> createVersion(
            @Valid @RequestBody VersionCreateRequest request,
            @RequestParam(required = false) String storageKey,
            @RequestParam(required = false) Long fileSize,
            @RequestParam(required = false) String checksum) {

        VersionDTO created = versionService.createVersion(request, storageKey, fileSize, checksum);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar versão", description = "Busca uma versão por ID")
    public ResponseEntity<VersionDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(versionService.findById(id));
    }

    @GetMapping("/document/{documentId}")
    @Operation(summary = "Listar versões", description = "Lista todas as versões de um documento")
    public ResponseEntity<PageResponse<VersionDTO>> findByDocumentId(
            @PathVariable UUID documentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(versionService.findByDocumentId(documentId, pageable));
    }

    @GetMapping("/document/{documentId}/latest")
    @Operation(summary = "Última versão", description = "Retorna a última versão de um documento")
    public ResponseEntity<VersionDTO> findLatestVersion(@PathVariable UUID documentId) {
        return ResponseEntity.ok(versionService.findLatestVersion(documentId));
    }

    @GetMapping("/document/{documentId}/history")
    @Operation(summary = "Histórico", description = "Retorna o histórico completo de alterações")
    public ResponseEntity<PageResponse<Object>> getHistory(
            @PathVariable UUID documentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(versionService.getHistory(documentId, pageable));
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restaurar versão", description = "Restaura uma versão anterior como nova versão")
    public ResponseEntity<VersionDTO> restoreVersion(@PathVariable UUID id) {
        return ResponseEntity.ok(versionService.restoreVersion(id));
    }
}
