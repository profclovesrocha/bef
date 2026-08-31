package com.docmanager.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilitário para geração de slugs a partir de nomes.
 * Útil para gerar chaves de armazenamento legíveis.
 */
public final class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private SlugUtils() {
        // Classe utilitária — não instanciar
    }

    /**
     * Converte um texto em slug URL-friendly.
     * Ex: "Relatório Anual 2024" → "relatorio-anual-2024"
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "unnamed";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ROOT)
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }
}
