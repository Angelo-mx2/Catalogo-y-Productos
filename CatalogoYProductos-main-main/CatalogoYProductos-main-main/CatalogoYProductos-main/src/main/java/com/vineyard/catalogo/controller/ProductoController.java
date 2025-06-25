package com.vineyard.catalogo.controller;

import com.vineyard.catalogo.model.Producto;
import com.vineyard.catalogo.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar productos", description = "Obtiene todos los productos del catálogo")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el catálogo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos para el producto")
    })
    @PostMapping
    public Producto guardarProducto(@Valid @RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto productoActualizado) {
        return productoService.obtenerProductoPorId(id)
                .map(productoExistente -> {
                    productoExistente.setNombre(productoActualizado.getNombre());
                    productoExistente.setPrecio(productoActualizado.getPrecio());
                    productoExistente.setDescripcion(productoActualizado.getDescripcion());
                    productoExistente.setFechaCaducidad(productoActualizado.getFechaCaducidad());

                    Producto productoGuardado = productoService.guardarProducto(productoExistente);
                    return ResponseEntity.ok(productoGuardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener producto con enlaces HATEOAS", description = "Obtiene un producto por ID con enlaces a recursos relacionados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado con HATEOAS"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoConLinks(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(producto -> {
                    EntityModel<Producto> recurso = EntityModel.of(producto);

                    recurso.add(WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerProductoConLinks(id)
                    ).withSelfRel());

                    recurso.add(WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(ProductoController.class).listarProductos()
                    ).withRel("all"));

                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


//localhost:8080/swagger-ui/index.html#/producto-controller/guardarProducto