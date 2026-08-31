package com.docmanager.storage.strategy;

import com.docmanager.common.exception.StorageException;
import com.docmanager.common.model.StorageType;
import com.docmanager.storage.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Estratégia de armazenamento em sistema de arquivos local.
 * Os arquivos são salvos em um diretório configurável.
 */
@Component
@Slf4j
public class LocalStorageStrategy implements StorageStrategy {

    private final Path rootLocation;

    public LocalStorageStrategy(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocalPath()).toAbsolutePath().normalize();
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("Diretório de armazenamento local inicializado: {}", rootLocation);
        } catch (IOException e) {
            throw new StorageException("Não foi possível criar o diretório de armazenamento", e);
        }
    }

    @Override
    public String store(String key, byte[] data, String contentType) {
        try {
            Path targetPath = rootLocation.resolve(key).normalize();

            // Segurança: impedir path traversal
            if (!targetPath.startsWith(rootLocation)) {
                throw new StorageException("Tentativa de armazenamento fora do diretório permitido");
            }

            // Cria subdiretórios se necessário
            Files.createDirectories(targetPath.getParent());

            Files.write(targetPath, data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Arquivo armazenado localmente: {} ({} bytes)", key, data.length);
            return key;
        } catch (IOException e) {
            throw new StorageException("Falha ao armazenar arquivo: " + key, e);
        }
    }

    @Override
    public byte[] retrieve(String key) {
        try {
            Path filePath = rootLocation.resolve(key).normalize();

            if (!filePath.startsWith(rootLocation)) {
                throw new StorageException("Tentativa de acesso fora do diretório permitido");
            }

            if (!Files.exists(filePath)) {
                throw new StorageException("Arquivo não encontrado: " + key);
            }

            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new StorageException("Falha ao recuperar arquivo: " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path filePath = rootLocation.resolve(key).normalize();

            if (!filePath.startsWith(rootLocation)) {
                throw new StorageException("Tentativa de exclusão fora do diretório permitido");
            }

            Files.deleteIfExists(filePath);
            log.info("Arquivo removido: {}", key);
        } catch (IOException e) {
            throw new StorageException("Falha ao remover arquivo: " + key, e);
        }
    }

    @Override
    public boolean exists(String key) {
        Path filePath = rootLocation.resolve(key).normalize();
        return filePath.startsWith(rootLocation) && Files.exists(filePath);
    }

    @Override
    public StorageType getType() {
        return StorageType.LOCAL;
    }
}
