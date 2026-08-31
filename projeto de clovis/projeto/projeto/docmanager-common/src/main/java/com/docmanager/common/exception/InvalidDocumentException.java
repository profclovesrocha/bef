package com.docmanager.common.exception;

/**
 * Exceção para documentos com dados inválidos.
 */
public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException(String message) {
        super(message);
    }
}
