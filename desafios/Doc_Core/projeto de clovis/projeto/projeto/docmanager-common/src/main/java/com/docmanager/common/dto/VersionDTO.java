package com.docmanager.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para representação de uma versão de documento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDTO {

    private UUID id;
    private UUID documentId;
    private Integer versionNumber;
    private String storageKey;
    private String changeDescription;
    private Long fileSize;
    private String checksum;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private String createdBy;
}
