package com.vineyard.catalogo.service;

import com.vineyard.catalogo.model.Producto;
import com.vineyard.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarProductos() {
        List<Producto> lista = Arrays.asList(
            new Producto(1L, "Agua", 1.5f, "bebida", LocalDate.of(2025, 12, 1)),
            new Producto(2L, "Galleta", 2.0f, "comida", LocalDate.of(2025, 11, 30))
        );

        when(productoRepository.findAll()).thenReturn(lista);

        List<Producto> resultado = productoService.listarProductos();
        assertEquals(2, resultado.size());
    }

    @Test
    void testGuardarProducto() {
        Producto producto = new Producto(null, "Pan", 1.0f, "comida", LocalDate.of(2025, 10, 10));
        Producto productoGuardado = new Producto(3L, "Pan", 1.0f, "comida", LocalDate.of(2025, 10, 10));

        when(productoRepository.save(producto)).thenReturn(productoGuardado);

        Producto resultado = productoService.guardarProducto(producto);
        assertNotNull(resultado.getId());
        assertEquals("Pan", resultado.getNombre());
    }

    @Test
    void testEliminarProducto() {
        Long id = 1L;

        productoService.eliminarProducto(id);

        verify(productoRepository, times(1)).deleteById(id);
    }

    @Test
    void testObtenerProductoPorId() {
        Producto producto = new Producto(1L, "Jugo", 2.0f, "bebida", LocalDate.of(2026, 1, 1));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.obtenerProductoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Jugo", resultado.get().getNombre());
    }
}
