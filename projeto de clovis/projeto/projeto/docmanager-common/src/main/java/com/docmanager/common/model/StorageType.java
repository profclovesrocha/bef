package com.docmanager.common.model;

/**
 * Tipos de armazenamento suportados pelo Storage Service.
 * O Strategy Pattern seleciona a implementação correta em runtime
 * com base neste enum.
 */
public enum StorageType {
    LOCAL,
    DATABASE,
    S3
}
