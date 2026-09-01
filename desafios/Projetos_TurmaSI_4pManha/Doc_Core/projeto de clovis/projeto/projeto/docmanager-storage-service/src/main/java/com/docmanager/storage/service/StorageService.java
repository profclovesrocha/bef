package com.docmanager.storage.service;

import com.docmanager.common.dto.StorageResultDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface do serviço de armazenamento.
 */
public interface StorageService {

    StorageResultDTO store(MultipartFile file);

    byte[] retrieve(String key);

    void delete(String key);

    boolean exists(String key);
}
