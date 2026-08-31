package com.docmanager.storage.config;

import com.docmanager.common.model.StorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de configuração do armazenamento.
 * Mapeadas a partir do prefixo {@code docmanager.storage} no application.yml.
 */
@Data
@Component
@ConfigurationProperties(prefix = "docmanager.storage")
public class StorageProperties {

    /**
     * Tipo de armazenamento ativo (LOCAL, S3, DATABASE).
     */
    private StorageType type = StorageType.LOCAL;

    /**
     * Caminho local para armazenamento de arquivos.
     */
    private String localPath = "./storage-data";
}
