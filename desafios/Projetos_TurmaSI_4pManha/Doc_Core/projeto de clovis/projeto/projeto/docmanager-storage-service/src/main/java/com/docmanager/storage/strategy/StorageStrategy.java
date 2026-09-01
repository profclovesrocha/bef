package com.docmanager.storage.strategy;

import com.docmanager.common.model.StorageType;

/**
 * Interface Strategy para operações de armazenamento.
 *
 * <p>Novas implementações de armazenamento (S3, Azure, GCS, etc.)
 * devem implementar esta interface. O sistema seleciona a estratégia
 * correta em runtime com base na configuração.</p>
 *
 * <p><b>Para adicionar um novo backend de armazenamento:</b></p>
 * <ol>
 *   <li>Crie uma classe que implemente {@code StorageStrategy}</li>
 *   <li>Anote-a com {@code @Component}</li>
 *   <li>Adicione o novo tipo em {@link StorageType}</li>
 *   <li>Configure {@code docmanager.storage.type} no application.yml</li>
 * </ol>
 */
public interface StorageStrategy {

    /**
     * Armazena dados com a chave fornecida.
     *
     * @param key         chave única de armazenamento
     * @param data        bytes do arquivo
     * @param contentType tipo MIME do conteúdo
     * @return a chave efetivamente usada para armazenamento
     */
    String store(String key, byte[] data, String contentType);

    /**
     * Recupera dados pela chave.
     *
     * @param key chave de armazenamento
     * @return bytes do arquivo
     */
    byte[] retrieve(String key);

    /**
     * Remove dados pela chave.
     *
     * @param key chave de armazenamento
     */
    void delete(String key);

    /**
     * Verifica se uma chave existe no armazenamento.
     *
     * @param key chave de armazenamento
     * @return true se existe
     */
    boolean exists(String key);

    /**
     * Retorna o tipo de armazenamento desta estratégia.
     */
    StorageType getType();
}
