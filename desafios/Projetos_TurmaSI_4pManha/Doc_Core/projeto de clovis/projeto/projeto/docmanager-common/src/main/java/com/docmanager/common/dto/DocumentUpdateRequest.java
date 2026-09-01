package com.docmanager.common.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para atualização de documentos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateRequest {

    @Size(min = 1, max = 255, message = "O nome deve ter entre 1 e 255 caracteres")
    private String name;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String description;
}
