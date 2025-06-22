package com.vineyard.catalogo.controller;

import com.vineyard.catalogo.model.Producto;
import com.vineyard.catalogo.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @PostMapping
    public Producto guardarProducto(@Valid @RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

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