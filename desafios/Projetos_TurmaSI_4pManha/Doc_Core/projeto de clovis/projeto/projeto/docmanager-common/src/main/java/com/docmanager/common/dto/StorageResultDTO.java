package com.docmanager.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO com resultado de operações de armazenamento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageResultDTO {

    private String storageKey;
    private String contentType;
    private Long size;
    private boolean success;
    private String message;
}
