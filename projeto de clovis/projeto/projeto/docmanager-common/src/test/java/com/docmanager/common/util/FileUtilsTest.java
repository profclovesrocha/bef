package com.docmanager.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileUtils Tests")
class FileUtilsTest {

    @Test
    @DisplayName("Deve extrair extensão corretamente")
    void shouldExtractExtension() {
        assertEquals("pdf", FileUtils.getExtension("arquivo.pdf"));
        assertEquals("docx", FileUtils.getExtension("relatorio.final.docx"));
        assertEquals("", FileUtils.getExtension("sem-extensao"));
        assertEquals("", FileUtils.getExtension(null));
    }

    @Test
    @DisplayName("Deve validar extensões permitidas")
    void shouldValidateAllowedExtensions() {
        assertTrue(FileUtils.isAllowedExtension("doc.pdf"));
        assertTrue(FileUtils.isAllowedExtension("imagem.png"));
        assertTrue(FileUtils.isAllowedExtension("planilha.xlsx"));
        assertFalse(FileUtils.isAllowedExtension("script.exe"));
        assertFalse(FileUtils.isAllowedExtension("virus.bat"));
    }

    @Test
    @DisplayName("Deve validar tamanho do arquivo")
    void shouldValidateFileSize() {
        assertTrue(FileUtils.isWithinSizeLimit(1024)); // 1 KB
        assertTrue(FileUtils.isWithinSizeLimit(50 * 1024 * 1024)); // 50 MB
        assertFalse(FileUtils.isWithinSizeLimit(51 * 1024 * 1024)); // 51 MB
        assertFalse(FileUtils.isWithinSizeLimit(0));
        assertFalse(FileUtils.isWithinSizeLimit(-1));
    }

    @Test
    @DisplayName("Deve sanitizar nome de arquivo")
    void shouldSanitizeFilename() {
        assertEquals("relat_rio_anual.pdf", FileUtils.sanitizeFilename("relatório anual.pdf"));
        assertEquals("unnamed", FileUtils.sanitizeFilename(null));
        assertEquals("normal.txt", FileUtils.sanitizeFilename("normal.txt"));
    }
}
