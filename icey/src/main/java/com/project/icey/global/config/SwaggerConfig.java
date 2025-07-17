package com.project.icey.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        // Bearer Token 인증 방식 설정
        io.swagger.v3.oas.models.security.SecurityScheme bearerAuth = new io.swagger.v3.oas.models.security.SecurityScheme()
                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        io.swagger.v3.oas.models.security.SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("ICEY")
                        .version("1.0.0")
                        .description("[SOLUX WEB 2 Team] ICEY API 문서입니다.")
                        .contact(new Contact()
                                .url("https://github.com/Solux-ChillGuy/ICEY-Backend"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(securityRequirement)
                .servers(List.of(
                        new Server().url("http://localhost:8080"),
                        new Server().url("https://icey-backend-1027532113913.asia-northeast3.run.app") // ✅ 배포 서버
                ));
    }
}

