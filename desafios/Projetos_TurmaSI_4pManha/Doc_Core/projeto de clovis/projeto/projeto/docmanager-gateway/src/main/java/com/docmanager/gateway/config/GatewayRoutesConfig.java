package com.docmanager.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de rotas do API Gateway.
 * Todas as requisições passam por aqui e são roteadas
 * para o microsserviço correto.
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ============================
                // Document Service → /api/v1/documents/**
                // ============================
                .route("document-service", r -> r
                        .path("/api/v1/documents/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "docmanager")
                                .retry(config -> config
                                        .setRetries(2)
                                        .setStatuses(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE)))
                        .uri("${docmanager.services.document-url:http://localhost:8081}"))

                // ============================
                // Storage Service → /api/v1/storage/**
                // ============================
                .route("storage-service", r -> r
                        .path("/api/v1/storage/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "docmanager"))
                        .uri("${docmanager.services.storage-url:http://localhost:8082}"))

                // ============================
                // Metadata Service → /api/v1/metadata/**
                // ============================
                .route("metadata-service", r -> r
                        .path("/api/v1/metadata/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "docmanager"))
                        .uri("${docmanager.services.metadata-url:http://localhost:8083}"))

                // ============================
                // Version Service → /api/v1/versions/**
                // ============================
                .route("version-service", r -> r
                        .path("/api/v1/versions/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "docmanager"))
                        .uri("${docmanager.services.version-url:http://localhost:8084}"))

                .build();
    }
}
