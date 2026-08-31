package com.docmanager.version.service;

import com.docmanager.common.dto.PageResponse;
import com.docmanager.common.dto.VersionCreateRequest;
import com.docmanager.common.dto.VersionDTO;
import com.docmanager.common.exception.VersionNotFoundException;
import com.docmanager.version.entity.ChangeLog;
import com.docmanager.version.entity.DocumentVersion;
import com.docmanager.version.mapper.VersionMapper;
import com.docmanager.version.repository.ChangeLogRepository;
import com.docmanager.version.repository.VersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VersionServiceImpl Tests")
class VersionServiceImplTest {

    @Mock
    private VersionRepository versionRepository;

    @Mock
    private ChangeLogRepository changeLogRepository;

    @Mock
    private VersionMapper versionMapper;

    @InjectMocks
    private VersionServiceImpl versionService;

    private UUID versionId;
    private UUID documentId;
    private DocumentVersion version;
    private VersionDTO versionDTO;
    private VersionCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        versionId = UUID.randomUUID();
        documentId = UUID.randomUUID();

        version = DocumentVersion.builder()
                .id(versionId)
                .documentId(documentId)
                .versionNumber(1)
                .storageKey("key-v1.pdf")
                .changeDescription("Versão inicial")
                .fileSize(2048L)
                .checksum("sha256hash")
                .createdAt(LocalDateTime.now())
                .createdBy("admin")
                .build();

        versionDTO = VersionDTO.builder()
                .id(versionId)
                .documentId(documentId)
                .versionNumber(1)
                .storageKey("key-v1.pdf")
                .changeDescription("Versão inicial")
                .fileSize(2048L)
                .checksum("sha256hash")
                .createdAt(LocalDateTime.now())
                .createdBy("admin")
                .build();

        createRequest = VersionCreateRequest.builder()
                .documentId(documentId)
                .changeDescription("Versão inicial")
                .createdBy("admin")
                .build();
    }

    @Test
    @DisplayName("Deve criar primeira versão com número 1")
    void shouldCreateFirstVersion() {
        when(versionRepository.countByDocumentId(documentId)).thenReturn(0L);
        when(versionRepository.save(any(DocumentVersion.class))).thenReturn(version);
        when(changeLogRepository.save(any(ChangeLog.class))).thenReturn(new ChangeLog());
        when(versionMapper.toDTO(version)).thenReturn(versionDTO);

        VersionDTO result = versionService.createVersion(createRequest, "key-v1.pdf", 2048L, "sha256hash");

        assertNotNull(result);
        assertEquals(1, result.getVersionNumber());
        verify(changeLogRepository).save(any(ChangeLog.class));
    }

    @Test
    @DisplayName("Deve criar segunda versão com número 2")
    void shouldCreateSecondVersion() {
        when(versionRepository.countByDocumentId(documentId)).thenReturn(1L);

        DocumentVersion v2 = DocumentVersion.builder()
                .id(UUID.randomUUID())
                .documentId(documentId)
                .versionNumber(2)
                .build();
        VersionDTO v2dto = VersionDTO.builder()
                .versionNumber(2)
                .build();

        when(versionRepository.save(any(DocumentVersion.class))).thenReturn(v2);
        when(changeLogRepository.save(any(ChangeLog.class))).thenReturn(new ChangeLog());
        when(versionMapper.toDTO(v2)).thenReturn(v2dto);

        VersionDTO result = versionService.createVersion(createRequest, "key-v2.pdf", 3000L, "hash2");

        assertEquals(2, result.getVersionNumber());
    }

    @Test
    @DisplayName("Deve buscar versão por ID")
    void shouldFindById() {
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(version));
        when(versionMapper.toDTO(version)).thenReturn(versionDTO);

        VersionDTO result = versionService.findById(versionId);

        assertNotNull(result);
        assertEquals(versionId, result.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando versão não encontrada")
    void shouldThrowWhenNotFound() {
        when(versionRepository.findById(versionId)).thenReturn(Optional.empty());

        assertThrows(VersionNotFoundException.class,
                () -> versionService.findById(versionId));
    }

    @Test
    @DisplayName("Deve retornar última versão do documento")
    void shouldFindLatestVersion() {
        when(versionRepository.findFirstByDocumentIdOrderByVersionNumberDesc(documentId))
                .thenReturn(Optional.of(version));
        when(versionMapper.toDTO(version)).thenReturn(versionDTO);

        VersionDTO result = versionService.findLatestVersion(documentId);

        assertNotNull(result);
        assertEquals(1, result.getVersionNumber());
    }

    @Test
    @DisplayName("Deve restaurar versão anterior como nova versão")
    void shouldRestoreVersion() {
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(version));
        when(versionRepository.countByDocumentId(documentId)).thenReturn(2L);

        DocumentVersion restored = DocumentVersion.builder()
                .id(UUID.randomUUID())
                .documentId(documentId)
                .versionNumber(3)
                .storageKey("key-v1.pdf")
                .build();
        VersionDTO restoredDTO = VersionDTO.builder()
                .versionNumber(3)
                .changeDescription("Restaurado a partir da versão 1")
                .build();

        when(versionRepository.save(any(DocumentVersion.class))).thenReturn(restored);
        when(changeLogRepository.save(any(ChangeLog.class))).thenReturn(new ChangeLog());
        when(versionMapper.toDTO(restored)).thenReturn(restoredDTO);

        VersionDTO result = versionService.restoreVersion(versionId);

        assertEquals(3, result.getVersionNumber());
        assertTrue(result.getChangeDescription().contains("Restaurado"));
    }

    @Test
    @DisplayName("Deve listar versões paginadas por documento")
    void shouldListVersionsByDocument() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocumentVersion> page = new PageImpl<>(List.of(version), pageable, 1);

        when(versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId, pageable))
                .thenReturn(page);
        when(versionMapper.toDTO(version)).thenReturn(versionDTO);

        PageResponse<VersionDTO> result = versionService.findByDocumentId(documentId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }
}
