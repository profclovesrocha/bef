package com.docmanager.storage.factory;

import com.docmanager.common.exception.StorageException;
import com.docmanager.common.model.StorageType;
import com.docmanager.storage.strategy.StorageStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory para seleção de estratégia de armazenamento em runtime.
 *
 * <p>Todas as implementações de {@link StorageStrategy} registradas como beans
 * são automaticamente injetadas. A factory seleciona a estratégia correta
 * com base no {@link StorageType} solicitado.</p>
 */
@Component
public class StorageStrategyFactory {

    private final Map<StorageType, StorageStrategy> strategies;

    /**
     * Injeta todas as implementações de StorageStrategy disponíveis.
     */
    public StorageStrategyFactory(List<StorageStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(StorageStrategy::getType, Function.identity()));
    }

    /**
     * Retorna a estratégia para o tipo especificado.
     *
     * @param type tipo de armazenamento
     * @return estratégia correspondente
     * @throws StorageException se o tipo não estiver registrado
     */
    public StorageStrategy getStrategy(StorageType type) {
        StorageStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new StorageException("Estratégia de armazenamento não encontrada: " + type);
        }
        return strategy;
    }
}
