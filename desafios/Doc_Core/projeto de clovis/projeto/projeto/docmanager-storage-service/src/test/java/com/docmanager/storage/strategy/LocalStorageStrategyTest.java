package com.docmanager.storage.strategy;

import com.docmanager.common.exception.StorageException;
import com.docmanager.storage.config.StorageProperties;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LocalStorageStrategy Tests")
class LocalStorageStrategyTest {

    private LocalStorageStrategy storageStrategy;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setLocalPath(tempDir.toString());
        storageStrategy = new LocalStorageStrategy(properties);
    }

    @Test
    @DisplayName("Deve armazenar e recuperar arquivo")
    void shouldStoreAndRetrieveFile() {
        byte[] data = "Conteúdo do arquivo de teste".getBytes();
        String key = "test-file.txt";

        String storedKey = storageStrategy.store(key, data, "text/plain");

        assertEquals(key, storedKey);
        assertTrue(storageStrategy.exists(key));

        byte[] retrieved = storageStrategy.retrieve(key);
        assertArrayEquals(data, retrieved);
    }

    @Test
    @DisplayName("Deve excluir arquivo")
    void shouldDeleteFile() {
        byte[] data = "temp".getBytes();
        String key = "to-delete.txt";

        storageStrategy.store(key, data, "text/plain");
        assertTrue(storageStrategy.exists(key));

        storageStrategy.delete(key);
        assertFalse(storageStrategy.exists(key));
    }

    @Test
    @DisplayName("Deve lançar exceção ao recuperar arquivo inexistente")
    void shouldThrowWhenRetrievingNonExistent() {
        assertThrows(StorageException.class,
                () -> storageStrategy.retrieve("nao-existe.txt"));
    }

    @Test
    @DisplayName("Deve criar subdiretórios automaticamente")
    void shouldCreateSubdirectories() {
        byte[] data = "nested".getBytes();
        String key = "subdir/nested-file.txt";

        String storedKey = storageStrategy.store(key, data, "text/plain");

        assertEquals(key, storedKey);
        assertTrue(storageStrategy.exists(key));
    }

    @Test
    @DisplayName("Deve retornar false para arquivo inexistente em exists()")
    void shouldReturnFalseForNonExistent() {
        assertFalse(storageStrategy.exists("inexistente.txt"));
    }

    @Test
    @DisplayName("Deve sobrescrever arquivo existente")
    void shouldOverwriteExistingFile() {
        String key = "overwrite.txt";
        storageStrategy.store(key, "versao 1".getBytes(), "text/plain");
        storageStrategy.store(key, "versao 2".getBytes(), "text/plain");

        byte[] retrieved = storageStrategy.retrieve(key);
        assertEquals("versao 2", new String(retrieved));
    }
}
