package com.docmanager.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Request DTO para criação/atualização de metadados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataRequest {

    @NotNull(message = "O ID do documento é obrigatório")
    private UUID documentId;

    @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres")
    private String category;

    private Set<String> tags;

    private List<MetadataEntryDTO> customProperties;
}
