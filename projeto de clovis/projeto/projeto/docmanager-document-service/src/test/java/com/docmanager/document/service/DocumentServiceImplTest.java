package com.docmanager.document.service;

import com.docmanager.common.dto.*;
import com.docmanager.common.exception.DocumentNotFoundException;
import com.docmanager.common.exception.InvalidDocumentException;
import com.docmanager.document.client.StorageClient;
import com.docmanager.document.entity.Document;
import com.docmanager.document.mapper.DocumentMapper;
import com.docmanager.document.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentServiceImpl Tests")
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private StorageClient storageClient;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Document document;
    private DocumentDTO documentDTO;
    private DocumentCreateRequest createRequest;
    private UUID documentId;

    @BeforeEach
    void setUp() {
        documentId = UUID.randomUUID();

        document = Document.builder()
                .id(documentId)
                .name("Relatório Anual")
                .description("Relatório financeiro anual 2024")
                .fileType("application/pdf")
                .fileSize(1024L)
                .storageKey("abc-123.pdf")
                .currentVersion(1)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .build();

        documentDTO = DocumentDTO.builder()
                .id(documentId)
                .name("Relatório Anual")
                .description("Relatório financeiro anual 2024")
                .fileType("application/pdf")
                .fileSize(1024L)
                .storageKey("abc-123.pdf")
                .currentVersion(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .build();

        createRequest = DocumentCreateRequest.builder()
                .name("Relatório Anual")
                .description("Relatório financeiro anual 2024")
                .createdBy("admin")
                .build();
    }

    @Nested
    @DisplayName("Criar Documento")
    class CreateTests {

        @Test
        @DisplayName("Deve criar documento sem arquivo")
        void shouldCreateDocumentWithoutFile() {
            when(documentMapper.toEntity(createRequest)).thenReturn(document);
            when(documentRepository.save(any(Document.class))).thenReturn(document);
            when(documentMapper.toDTO(document)).thenReturn(documentDTO);

            DocumentDTO result = documentService.create(createRequest, null);

            assertNotNull(result);
            assertEquals("Relatório Anual", result.getName());
            verify(documentRepository).save(any(Document.class));
            verify(storageClient, never()).upload(any());
        }

        @Test
        @DisplayName("Deve criar documento com upload de arquivo")
        void shouldCreateDocumentWithFile() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "relatorio.pdf", "application/pdf",
                    "conteudo do pdf".getBytes());

            StorageResultDTO storageResult = StorageResultDTO.builder()
                    .storageKey("uuid-123.pdf")
                    .success(true)
                    .build();

            when(documentMapper.toEntity(createRequest)).thenReturn(document);
            when(storageClient.upload(file)).thenReturn(storageResult);
            when(documentRepository.save(any(Document.class))).thenReturn(document);
            when(documentMapper.toDTO(document)).thenReturn(documentDTO);

            DocumentDTO result = documentService.create(createRequest, file);

            assertNotNull(result);
            verify(storageClient).upload(file);
            verify(documentRepository).save(any(Document.class));
        }
    }

    @Nested
    @DisplayName("Buscar Documento")
    class FindTests {

        @Test
        @DisplayName("Deve retornar documento por ID")
        void shouldFindDocumentById() {
            when(documentRepository.findByIdAndDeletedFalse(documentId))
                    .thenReturn(Optional.of(document));
            when(documentMapper.toDTO(document)).thenReturn(documentDTO);

            DocumentDTO result = documentService.findById(documentId);

            assertNotNull(result);
            assertEquals(documentId, result.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção quando documento não encontrado")
        void shouldThrowWhenDocumentNotFound() {
            when(documentRepository.findByIdAndDeletedFalse(documentId))
                    .thenReturn(Optional.empty());

            assertThrows(DocumentNotFoundException.class,
                    () -> documentService.findById(documentId));
        }

        @Test
        @DisplayName("Deve retornar lista paginada de documentos")
        void shouldReturnPaginatedDocuments() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Document> page = new PageImpl<>(List.of(document), pageable, 1);

            when(documentRepository.findByDeletedFalse(pageable)).thenReturn(page);
            when(documentMapper.toDTO(document)).thenReturn(documentDTO);

            PageResponse<DocumentDTO> result = documentService.findAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            assertTrue(result.isFirst());
            assertTrue(result.isLast());
        }
    }

    @Nested
    @DisplayName("Atualizar Documento")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar nome e descrição do documento")
        void shouldUpdateDocument() {
            DocumentUpdateRequest updateRequest = DocumentUpdateRequest.builder()
                    .name("Novo Nome")
                    .description("Nova descrição")
                    .build();

            when(documentRepository.findByIdAndDeletedFalse(documentId))
                    .thenReturn(Optional.of(document));
            when(documentRepository.save(any(Document.class))).thenReturn(document);
            when(documentMapper.toDTO(document)).thenReturn(documentDTO);

            DocumentDTO result = documentService.update(documentId, updateRequest);

            assertNotNull(result);
            verify(documentRepository).save(document);
        }
    }

    @Nested
    @DisplayName("Excluir Documento")
    class DeleteTests {

        @Test
        @DisplayName("Deve fazer soft delete do documento")
        void shouldSoftDeleteDocument() {
            when(documentRepository.findByIdAndDeletedFalse(documentId))
                    .thenReturn(Optional.of(document));
            when(documentRepository.save(any(Document.class))).thenReturn(document);

            documentService.delete(documentId);

            assertTrue(document.isDeleted());
            verify(documentRepository).save(document);
            verify(storageClient).delete(document.getStorageKey());
        }

        @Test
        @DisplayName("Deve lançar exceção ao excluir documento inexistente")
        void shouldThrowWhenDeletingNonExistent() {
            when(documentRepository.findByIdAndDeletedFalse(documentId))
                    .thenReturn(Optional.empty());

            assertThrows(DocumentNotFoundException.class,
                    () -> documentService.delete(documentId));
        }
    }

    @Nested
    @DisplayName("Download de Arquivo")
    class DownloadTests {

        @Test
        @DisplayName("Deve lançar exceção quando documento não tem arquivo")
        void shouldThrowWhenNoFile() {
            document.setStorageKey(null);
            when(documentRepository.findByIdAndDeletedFalse(documentId))
                    .thenReturn(Optional.of(document));

            assertThrows(InvalidDocumentException.class,
                    () -> documentService.downloadFile(documentId));
        }
    }
}
