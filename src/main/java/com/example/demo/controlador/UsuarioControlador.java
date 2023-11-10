package com.example.demo.controlador;

import com.example.demo.dto.ProductoDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.error.ProductoNotFoundException;
import com.example.demo.error.UsuarioNotFoundException;
import com.example.demo.modelo.Producto;
import com.example.demo.modelo.Usuario;
import com.example.demo.repos.UsuarioRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/usuario")
public class UsuarioControlador {

    private final UsuarioRepositorio usuarioRepositorio;
    public UsuarioControlador(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping("/")
    public List<UsuarioDTO> getUsuarios() {
        List<UsuarioDTO> resultado = new ArrayList<>();
        for (Usuario usuario: usuarioRepositorio.findAll()) resultado.add(new UsuarioDTO(usuario));
        return resultado;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getProductoById(@PathVariable Long id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PostMapping("/")
    public ResponseEntity<UsuarioDTO> createUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            // Convert UsuarioDTO to Usuario
            Usuario usuario = new Usuario();
            usuario.setName(usuarioDTO.getName());
            usuario.setEmail(usuarioDTO.getEmail());

            // You may need to set other properties based on your requirements

            // Save the Usuario
            Usuario createdUsuario = usuarioRepositorio.save(usuario);

            // Create UsuarioDTO for the response
            UsuarioDTO createdUsuarioDTO = new UsuarioDTO(createdUsuario);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdUsuarioDTO.getId())
                    .toUri();

            return ResponseEntity.created(location).body(createdUsuarioDTO);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioRepositorio.findById(id)
                .map(existingUsuario -> {
                    existingUsuario.setName(usuarioDTO.getName());
                    existingUsuario.setEmail(usuarioDTO.getEmail());
                    usuarioRepositorio.save(existingUsuario);

                    // Include the URI of the updated resource in the response
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(id)
                            .toUri();

                    // Convert the updated Usuario to UsuarioDTO for response
                    UsuarioDTO updatedUsuarioDTO = new UsuarioDTO(existingUsuario);

                    return ResponseEntity.ok().location(location).body(updatedUsuarioDTO);
                })
                .orElseThrow(UsuarioNotFoundException::new);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable Long id) {
        return usuarioRepositorio.findById(id)
                .map(usuario -> {
                    usuarioRepositorio.delete(usuario);

                    // Include the URI to retrieve all elements in the response
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/")
                            .build()
                            .toUri();

                    return ResponseEntity.noContent().location(location).build();
                })
                .orElseThrow(UsuarioNotFoundException::new);
    }
}
