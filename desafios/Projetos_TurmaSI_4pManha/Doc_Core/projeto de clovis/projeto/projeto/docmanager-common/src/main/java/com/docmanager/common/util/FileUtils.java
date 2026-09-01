package com.docmanager.common.util;

import java.util.Set;

/**
 * Utilitários para validação e manipulação de arquivos.
 */
public final class FileUtils {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "csv", "json", "xml", "html",
            "png", "jpg", "jpeg", "gif", "svg",
            "zip", "rar", "7z"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    private FileUtils() {
        // Classe utilitária — não instanciar
    }

    /**
     * Extrai a extensão de um nome de arquivo.
     *
     * @param filename nome do arquivo
     * @return extensão em lowercase ou string vazia
     */
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Verifica se a extensão do arquivo é permitida.
     */
    public static boolean isAllowedExtension(String filename) {
        return ALLOWED_EXTENSIONS.contains(getExtension(filename));
    }

    /**
     * Verifica se o tamanho do arquivo está dentro do limite.
     */
    public static boolean isWithinSizeLimit(long size) {
        return size > 0 && size <= MAX_FILE_SIZE;
    }

    /**
     * Gera um nome de arquivo seguro removendo caracteres especiais.
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Retorna o tamanho máximo permitido em bytes.
     */
    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}
