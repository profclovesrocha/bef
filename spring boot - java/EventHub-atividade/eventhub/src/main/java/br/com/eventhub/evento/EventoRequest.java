package br.com.eventhub.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter até 255 caracteres") String nome,
        @NotBlank(message = "O local é obrigatório")
        @Size(max = 255, message = "O local deve ter até 255 caracteres") String local,
        @NotNull(message = "A data é obrigatória") LocalDateTime data,
        @NotNull(message = "A quantidade de vagas é obrigatória")
        @Positive(message = "O evento precisa possuir pelo menos uma vaga") Integer vagas
) { }
