package com.docmanager.common.exception;

/**
 * Exceção para versão de documento não encontrada.
 */
public class VersionNotFoundException extends RuntimeException {

    public VersionNotFoundException(String message) {
        super(message);
    }

    public VersionNotFoundException(java.util.UUID id) {
        super("Versão não encontrada com ID: " + id);
    }
}
