package com.docmanager.common.exception;

/**
 * Exceção para recurso de metadados não encontrado.
 */
public class MetadataNotFoundException extends RuntimeException {

    public MetadataNotFoundException(String message) {
        super(message);
    }

    public MetadataNotFoundException(java.util.UUID id) {
        super("Metadado não encontrado com ID: " + id);
    }
}
