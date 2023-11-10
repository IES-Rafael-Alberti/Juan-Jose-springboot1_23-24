package com.example.demo.controlador;

import com.example.demo.dto.ProductoDTO;
import com.example.demo.error.DuplicateProductException;
import com.example.demo.error.GlobalExceptionHandler;
import com.example.demo.error.ProductoNotFoundException;
import com.example.demo.repos.ProductoRepositorio;
import com.example.demo.repos.UsuarioRepositorio;
import com.example.demo.error.UsuarioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.modelo.Producto;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/producto")
public class ProductoControlador {
    private final ProductoRepositorio productoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private  GlobalExceptionHandler exceptionHandler;

    public ProductoControlador(ProductoRepositorio productoRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping("/")
    public ResponseEntity<List<ProductoDTO>> getProductos() {
        List<ProductoDTO> productos = new ArrayList<>();
        for (Producto producto: productoRepositorio.findAll()) productos.add(new ProductoDTO(producto));
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(ProductoNotFoundException::new);

        ProductoDTO productoDTO = new ProductoDTO(producto);
        return ResponseEntity.ok(productoDTO);
    }

    @PostMapping("/")
    public ResponseEntity<Producto> createProducto(@Valid @RequestBody Producto producto) {
        Producto createdProducto = productoRepositorio.save(producto);
        // Generate the URI to access the created product
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProducto.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdProducto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoRepositorio.findById(id)
                .map(existingProducto -> {
                    existingProducto.setName(producto.getName());
                    existingProducto.setPrice(producto.getPrice());
                    Producto updatedProducto = productoRepositorio.save(existingProducto);

                    // Generate the URI to access the updated product
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(updatedProducto.getId())
                            .toUri();

                    return ResponseEntity.ok().location(location).body(updatedProducto);
                })
                .orElseThrow(ProductoNotFoundException::new );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        return productoRepositorio.findById(id)
                .map(producto -> {
                    productoRepositorio.delete(producto);
                    // Include the URI to retrieve all products for the user
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .replacePath("/productos")
                            .build()
                            .toUri();
                    return ResponseEntity.ok().location(location).body("Producto with the id "+id   +"has been deleted");
                })
                .orElseThrow(ProductoNotFoundException::new);
    }


    @PostMapping("/{id}/productos")
    public ResponseEntity<ProductoDTO> addProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoRepositorio.findById(id)
                .map(usuario -> {
                    producto.setUsuario(usuario.getUsuario());
                    ProductoDTO createdProducto = new ProductoDTO(productoRepositorio.save(producto));
                    // Include the URI to access the created product
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{productoId}")
                            .buildAndExpand(createdProducto.getId())
                            .toUri();
                    return ResponseEntity.created(location).body(createdProducto);
                })
                .orElseThrow(ProductoNotFoundException::new);
    }



    @PutMapping("/{id}/productos/{productoId}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @PathVariable Long productoId, @Valid @RequestBody Producto productoRequest) {

        if (!usuarioRepositorio.existsById(id)) {
            throw new UsuarioNotFoundException();
        }
        return productoRepositorio.findById(productoId)
                .map(producto -> {
                    producto.setName(productoRequest.getName());
                    producto.setPrice(productoRequest.getPrice());
                    Producto updatedProducto = productoRepositorio.save(producto);

                    // Generate the URI to access the updated product
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .replacePath("/productos/" + productoId)
                            .build()
                            .toUri();

                    return ResponseEntity.ok().location(location).body(updatedProducto);
                })
                .orElseThrow(ProductoNotFoundException::new);
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ProductoDTO>> getProductosByUsuario(@PathVariable Long usuarioId) {
        List<ProductoDTO> productos = new ArrayList<>();
        for (Producto producto: productoRepositorio.findByUsuarioId(usuarioId)) productos.add(new ProductoDTO(producto));
        return ResponseEntity.ok(productos);
    }
}
