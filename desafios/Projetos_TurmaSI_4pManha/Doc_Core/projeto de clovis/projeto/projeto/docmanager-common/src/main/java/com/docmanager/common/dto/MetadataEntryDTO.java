package com.docmanager.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para pares chave-valor de metadados customizados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntryDTO {

    private String key;
    private String value;
}
