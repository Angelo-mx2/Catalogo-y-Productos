package com.vineyard.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vineyard.catalogo.model.Producto;
import com.vineyard.catalogo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarProductos() throws Exception {
        Producto p1 = new Producto(1L, "Galleta", 2.5f, "comida", LocalDate.of(2025, 12, 31));
        Producto p2 = new Producto(2L, "Agua", 1.0f, "bebida", LocalDate.of(2026, 1, 15));

        when(productoService.listarProductos()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Galleta"))
                .andExpect(jsonPath("$[1].nombre").value("Agua"));
    }

    @Test
    void testGuardarProducto() throws Exception {
        Producto nuevo = new Producto(null, "Pan", 1.2f, "comida", LocalDate.of(2025, 10, 10));
        Producto guardado = new Producto(3L, "Pan", 1.2f, "comida", LocalDate.of(2025, 10, 10));

        when(productoService.guardarProducto(any())).thenReturn(guardado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Pan"));
    }

    @Test
    void testEliminarProducto() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testActualizarProducto() throws Exception {
        Producto existente = new Producto(1L, "Agua", 1.0f, "bebida", LocalDate.of(2025, 12, 31));
        Producto actualizado = new Producto(null, "Agua Mineral", 1.5f, "bebida", LocalDate.of(2026, 1, 1));
        Producto resultado = new Producto(1L, "Agua Mineral", 1.5f, "bebida", LocalDate.of(2026, 1, 1));

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(existente));
        when(productoService.guardarProducto(any())).thenReturn(resultado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agua Mineral"))
                .andExpect(jsonPath("$.precio").value(1.5));
    }

    @Test
    void testActualizarProductoNoEncontrado() throws Exception {
        Producto actualizado = new Producto(null, "Agua Mineral", 1.5f, "bebida", LocalDate.of(2026, 1, 1));

        when(productoService.obtenerProductoPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isNotFound());
    }
}
