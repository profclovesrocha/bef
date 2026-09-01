package com.docmanager.version.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VersionConfig {

    @Bean
    public OpenAPI versionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Version Service API")
                        .description("Microsserviço de versionamento e histórico de documentos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DocManager Team")));
    }
}
