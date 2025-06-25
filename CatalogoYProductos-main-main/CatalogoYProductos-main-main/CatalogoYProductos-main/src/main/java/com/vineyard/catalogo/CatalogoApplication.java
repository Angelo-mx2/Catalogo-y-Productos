package com.vineyard.catalogo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "Microservicio de Catálogo de Productos",
        version = "1.0",
        description = "API REST para la gestión de productos del catálogo, incluyendo comidas y bebidas."
    )
)
@SpringBootApplication
public class CatalogoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogoApplication.class, args);
    }
}



