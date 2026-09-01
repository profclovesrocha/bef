package com.docmanager.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para criação de documentos.
 * Inclui validações Jakarta Validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {

    @NotBlank(message = "O nome do documento é obrigatório")
    @Size(min = 1, max = 255, message = "O nome deve ter entre 1 e 255 caracteres")
    private String name;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String description;

    private String createdBy;
}
