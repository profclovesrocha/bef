package com.docmanager.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SlugUtils Tests")
class SlugUtilsTest {

    @Test
    @DisplayName("Deve gerar slug correto")
    void shouldGenerateSlug() {
        assertEquals("relatorio-anual-2024", SlugUtils.toSlug("Relatório Anual 2024"));
        assertEquals("hello-world", SlugUtils.toSlug("Hello World"));
        assertEquals("unnamed", SlugUtils.toSlug(null));
        assertEquals("unnamed", SlugUtils.toSlug("  "));
    }

    @Test
    @DisplayName("Deve remover caracteres especiais")
    void shouldRemoveSpecialCharacters() {
        String slug = SlugUtils.toSlug("Café & Résumé!");
        assertFalse(slug.contains("&"));
        assertFalse(slug.contains("!"));
    }

    @Test
    @DisplayName("Deve remover hifens duplos")
    void shouldRemoveDoubleHyphens() {
        String slug = SlugUtils.toSlug("hello   world");
        assertFalse(slug.contains("--"));
    }
}
