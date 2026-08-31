package com.docmanager.common.exception;

/**
 * Exceção para erros de armazenamento de arquivos.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
