package com.docmanager.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO para criação de uma nova versão.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionCreateRequest {

    @NotNull(message = "O ID do documento é obrigatório")
    private UUID documentId;

    @Size(max = 500, message = "A descrição da alteração deve ter no máximo 500 caracteres")
    private String changeDescription;

    private String createdBy;
}
