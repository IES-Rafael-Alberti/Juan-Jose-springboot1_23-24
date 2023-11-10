package com.example.demo.dto;

import com.example.demo.modelo.Producto;
import com.example.demo.modelo.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class UsuarioDTO {
    Long id;
    String name;
    String email;
    List<Long> productos = new ArrayList<>();

    public UsuarioDTO(Usuario usuario){
        id = usuario.getId();
        name = usuario.getName();
        email = usuario.getEmail();

        // Check if productos is not null before iterating
        if (usuario.getProductos() != null) {
            for (Producto producto: usuario.getProductos()){
                productos.add(producto.getId());
            }
        }
    }
}
