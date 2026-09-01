package com.docmanager.common.exception;

/**
 * Exceção lançada quando um documento não é encontrado.
 */
public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public DocumentNotFoundException(java.util.UUID id) {
        super("Documento não encontrado com ID: " + id);
    }
}
