package com.docmanager.storage.controller;

import com.docmanager.common.dto.StorageResultDTO;
import com.docmanager.storage.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller REST para operações de armazenamento.
 */
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "Armazenamento", description = "API para armazenamento de arquivos")
public class StorageController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de arquivo", description = "Armazena um arquivo e retorna a chave de referência")
    public ResponseEntity<StorageResultDTO> upload(@RequestPart("file") MultipartFile file) {
        StorageResultDTO result = storageService.store(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{key}")
    @Operation(summary = "Download de arquivo", description = "Recupera um arquivo pela chave de armazenamento")
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        byte[] data = storageService.retrieve(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Remover arquivo", description = "Remove um arquivo do armazenamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String key) {
        storageService.delete(key);
    }

    @GetMapping("/{key}/exists")
    @Operation(summary = "Verificar existência", description = "Verifica se um arquivo existe no armazenamento")
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable String key) {
        boolean exists = storageService.exists(key);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
