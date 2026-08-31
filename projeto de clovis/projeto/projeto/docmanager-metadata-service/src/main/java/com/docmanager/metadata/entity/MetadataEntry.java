package com.docmanager.metadata.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade para propriedades customizadas (pares chave-valor)
 * associadas a um metadado.
 */
@Entity
@Table(name = "metadata_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entry_key", nullable = false)
    private String key;

    @Column(name = "entry_value", length = 2000)
    private String value;
}
