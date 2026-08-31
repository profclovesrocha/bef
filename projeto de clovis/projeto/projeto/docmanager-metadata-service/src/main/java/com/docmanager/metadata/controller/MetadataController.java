package com.docmanager.metadata.controller;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataRequest;
import com.docmanager.common.dto.PageResponse;
import com.docmanager.metadata.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
@Tag(name = "Metadados", description = "API para gerenciamento de metadados de documentos")
public class MetadataController {

    private final MetadataService metadataService;

    @PostMapping
    @Operation(summary = "Criar metadados", description = "Cria metadados para um documento")
    public ResponseEntity<MetadataDTO> create(@Valid @RequestBody MetadataRequest request) {
        MetadataDTO created = metadataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/document/{documentId}")
    @Operation(summary = "Buscar por documento", description = "Lista metadados de um documento")
    public ResponseEntity<List<MetadataDTO>> findByDocumentId(@PathVariable UUID documentId) {
        return ResponseEntity.ok(metadataService.findByDocumentId(documentId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca metadado por ID")
    public ResponseEntity<MetadataDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(metadataService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar metadados", description = "Atualiza metadados existentes")
    public ResponseEntity<MetadataDTO> update(@PathVariable UUID id,
                                               @Valid @RequestBody MetadataRequest request) {
        return ResponseEntity.ok(metadataService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir metadados")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        metadataService.delete(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar metadados", description = "Busca por categoria e/ou tag")
    public ResponseEntity<PageResponse<MetadataDTO>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(metadataService.search(category, tag, pageable));
    }
}
