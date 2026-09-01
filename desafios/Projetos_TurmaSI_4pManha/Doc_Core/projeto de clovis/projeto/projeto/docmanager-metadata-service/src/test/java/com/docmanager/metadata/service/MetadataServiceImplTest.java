package com.docmanager.metadata.service;

import com.docmanager.common.dto.MetadataDTO;
import com.docmanager.common.dto.MetadataEntryDTO;
import com.docmanager.common.dto.MetadataRequest;
import com.docmanager.common.dto.PageResponse;
import com.docmanager.common.exception.MetadataNotFoundException;
import com.docmanager.metadata.entity.Metadata;
import com.docmanager.metadata.entity.MetadataEntry;
import com.docmanager.metadata.mapper.MetadataMapper;
import com.docmanager.metadata.repository.MetadataRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MetadataServiceImpl Tests")
class MetadataServiceImplTest {

    @Mock
    private MetadataRepository metadataRepository;

    @Mock
    private MetadataMapper metadataMapper;

    @InjectMocks
    private MetadataServiceImpl metadataService;

    private UUID metadataId;
    private UUID documentId;
    private Metadata metadata;
    private MetadataDTO metadataDTO;
    private MetadataRequest request;

    @BeforeEach
    void setUp() {
        metadataId = UUID.randomUUID();
        documentId = UUID.randomUUID();

        metadata = Metadata.builder()
                .id(metadataId)
                .documentId(documentId)
                .category("Financeiro")
                .tags(new HashSet<>(Set.of("anual", "receita")))
                .customProperties(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        metadataDTO = MetadataDTO.builder()
                .id(metadataId)
                .documentId(documentId)
                .category("Financeiro")
                .tags(Set.of("anual", "receita"))
                .customProperties(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        request = MetadataRequest.builder()
                .documentId(documentId)
                .category("Financeiro")
                .tags(Set.of("anual", "receita"))
                .customProperties(List.of())
                .build();
    }

    @Test
    @DisplayName("Deve criar metadados")
    void shouldCreateMetadata() {
        when(metadataMapper.toEntity(request)).thenReturn(metadata);
        when(metadataRepository.save(metadata)).thenReturn(metadata);
        when(metadataMapper.toDTO(metadata)).thenReturn(metadataDTO);

        MetadataDTO result = metadataService.create(request);

        assertNotNull(result);
        assertEquals("Financeiro", result.getCategory());
        verify(metadataRepository).save(metadata);
    }

    @Test
    @DisplayName("Deve buscar metadados por ID do documento")
    void shouldFindByDocumentId() {
        when(metadataRepository.findByDocumentId(documentId)).thenReturn(List.of(metadata));
        when(metadataMapper.toDTO(metadata)).thenReturn(metadataDTO);

        List<MetadataDTO> result = metadataService.findByDocumentId(documentId);

        assertEquals(1, result.size());
        assertEquals(documentId, result.get(0).getDocumentId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando metadado não encontrado")
    void shouldThrowWhenNotFound() {
        when(metadataRepository.findById(metadataId)).thenReturn(Optional.empty());

        assertThrows(MetadataNotFoundException.class,
                () -> metadataService.findById(metadataId));
    }

    @Test
    @DisplayName("Deve buscar metadados por categoria e tag")
    void shouldSearchByCategoryAndTag() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Metadata> page = new PageImpl<>(List.of(metadata), pageable, 1);

        when(metadataRepository.search("Financeiro", "anual", pageable)).thenReturn(page);
        when(metadataMapper.toDTO(metadata)).thenReturn(metadataDTO);

        PageResponse<MetadataDTO> result = metadataService.search("Financeiro", "anual", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve excluir metadado por ID")
    void shouldDeleteById() {
        when(metadataRepository.findById(metadataId)).thenReturn(Optional.of(metadata));

        metadataService.delete(metadataId);

        verify(metadataRepository).deleteById(metadataId);
    }
}
