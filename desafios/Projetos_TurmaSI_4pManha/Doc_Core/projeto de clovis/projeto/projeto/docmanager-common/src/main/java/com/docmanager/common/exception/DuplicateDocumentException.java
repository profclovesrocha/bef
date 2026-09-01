package com.docmanager.common.exception;

/**
 * Exceção lançada quando se tenta criar um documento duplicado.
 */
public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException(String message) {
        super(message);
    }
}
