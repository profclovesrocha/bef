package com.docmanager.storage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public OpenAPI storageServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Storage Service API")
                        .description("Microsserviço de armazenamento de arquivos com Strategy Pattern")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DocManager Team")));
    }
}
