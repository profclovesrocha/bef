package com.docmanager.storage.service;

import com.docmanager.common.dto.StorageResultDTO;
import com.docmanager.common.model.StorageType;
import com.docmanager.storage.config.StorageProperties;
import com.docmanager.storage.factory.StorageStrategyFactory;
import com.docmanager.storage.strategy.StorageStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StorageServiceImpl Tests")
class StorageServiceImplTest {

    @Mock
    private StorageStrategyFactory factory;

    @Mock
    private StorageStrategy strategy;

    private StorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setType(StorageType.LOCAL);

        when(factory.getStrategy(StorageType.LOCAL)).thenReturn(strategy);
        storageService = new StorageServiceImpl(factory, properties);
    }

    @Test
    @DisplayName("Deve armazenar arquivo e retornar resultado")
    void shouldStoreFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "conteudo".getBytes());

        when(strategy.store(anyString(), any(byte[].class), eq("application/pdf")))
                .thenAnswer(inv -> inv.getArgument(0));

        StorageResultDTO result = storageService.store(file);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getStorageKey().endsWith(".pdf"));
        assertEquals("application/pdf", result.getContentType());
        verify(strategy).store(anyString(), any(byte[].class), eq("application/pdf"));
    }

    @Test
    @DisplayName("Deve recuperar arquivo do strategy")
    void shouldRetrieveFile() {
        byte[] expectedData = "dados".getBytes();
        when(strategy.retrieve("key")).thenReturn(expectedData);

        byte[] result = storageService.retrieve("key");

        assertArrayEquals(expectedData, result);
    }

    @Test
    @DisplayName("Deve deletar arquivo via strategy")
    void shouldDeleteFile() {
        storageService.delete("key");
        verify(strategy).delete("key");
    }

    @Test
    @DisplayName("Deve verificar existência via strategy")
    void shouldCheckExists() {
        when(strategy.exists("key")).thenReturn(true);
        assertTrue(storageService.exists("key"));
    }
}
