package com.example.demo.controlador;

import com.example.demo.dto.ProductoDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.error.DuplicateProductException;
import com.example.demo.error.GlobalExceptionHandler;
import com.example.demo.error.ProductoNotFoundException;
import com.example.demo.modelo.Usuario;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/producto")
public class ProductoControlador {
    private final ProductoRepositorio productoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ProductoControlador(ProductoRepositorio productoRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping("/")
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<ProductoDTO> productosDTO = productoRepositorio.findAll()
                .stream()
                .map(ProductoDTO::new) // Assuming you have a constructor in ProductoDTO that takes Producto
                .toList();

        if (productosDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productosDTO, HttpStatus.OK);
        }
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
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        try {
            // Entity Retrieval
            Producto existingProducto = productoRepositorio.findById(id)
                    .orElseThrow(ProductoNotFoundException::new);

            // Update Entity
            existingProducto.setName(productoDTO.getName());
            existingProducto.setPrice(productoDTO.getPrice());

            // User Mapping
            Long usuarioId = productoDTO.getUsuario();
            if (usuarioId != null) {
                Usuario usuario = new Usuario();
                usuario.setId(usuarioId);
                existingProducto.setUsuario(usuario);
            } else {
                // Handle the case where usuarioId is null
                return ResponseEntity.badRequest().body("Usuario ID cannot be null.");
            }

            // Save and Return Response
            Producto updatedProducto = productoRepositorio.save(existingProducto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(updatedProducto.getId())
                    .toUri();

            return ResponseEntity.ok().location(location).body(updatedProducto);
        } catch (ProductoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
                    return ResponseEntity.ok().location(location).body("Producto with the id "+id   +" has been deleted");
                })
                .orElseThrow(ProductoNotFoundException::new);
    }


    @PostMapping("/{id}/productos")
    public ResponseEntity<ProductoDTO> addProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        try {
            // Attempt to find the user by ID
            Usuario usuario = usuarioRepositorio.findById(id)
                    .orElseThrow(ProductoNotFoundException::new);

            // Associate the user with the product
            producto.setUsuario(usuario);

            // Save the product
            Producto savedProducto = productoRepositorio.save(producto);

            // Create ProductoDTO from the saved product
            ProductoDTO createdProducto = new ProductoDTO(savedProducto);

            // Include the URI to access the created product
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{productoId}")
                    .buildAndExpand(createdProducto.getId())
                    .toUri();

            return ResponseEntity.created(location).body(createdProducto);
        } catch (ProductoNotFoundException ex) {
            // Handle the exception, for example, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}/productos/{productoId}")
    public ResponseEntity<ProductoDTO> updateProducto(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @Valid @RequestBody ProductoDTO productoRequest) {

        System.out.println("Received request to update product with ID " + productoId + " for user with ID " + id);

        // Check if the user exists
        if (!usuarioRepositorio.existsById(id)) {
            System.out.println("User with ID " + id + " not found");
            throw new UsuarioNotFoundException();
        }

        // Check if the product exists
        Producto existingProducto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> {
                    System.out.println("Product with ID " + productoId + " not found");
                    return new ProductoNotFoundException();
                });

        System.out.println("Existing product: " + existingProducto);

        // Update the existing product
        existingProducto.setName(productoRequest.getName());
        existingProducto.setPrice(productoRequest.getPrice());

        // Associate the product with the specified user
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> {
                    System.out.println("User with ID " + id + " not found");
                    throw new UsuarioNotFoundException();
                });
        existingProducto.setUsuario(usuario);

        // Save the updated product
        Producto updatedProducto = productoRepositorio.save(existingProducto);

        System.out.println("Updated product: " + updatedProducto);

        // Convert the updated entity to DTO
        ProductoDTO updatedProductoDTO = new ProductoDTO(updatedProducto);

        // Generate the URI to access the updated product
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/productos/" + productoId)
                .build()
                .toUri();

        return ResponseEntity.ok().location(location).body(updatedProductoDTO);
    }




    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ProductoDTO>> getProductosByUsuario(@PathVariable Long usuarioId) {
        List<ProductoDTO> productos = new ArrayList<>();
        for (Producto producto: productoRepositorio.findByUsuarioId(usuarioId)) productos.add(new ProductoDTO(producto));
        return ResponseEntity.ok(productos);
    }
}
