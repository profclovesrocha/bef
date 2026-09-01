package com.docmanager.metadata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetadataConfig {

    @Bean
    public OpenAPI metadataServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Metadata Service API")
                        .description("Microsserviço de gerenciamento de metadados — tags, categorias, propriedades")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DocManager Team")));
    }
}
