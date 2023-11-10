package com.example.demo.dto;

import com.example.demo.modelo.Producto;
import com.example.demo.modelo.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
public class ProductoDTO {

    Long id;
    String name;
    BigDecimal price;
    Long usuario;

    public ProductoDTO(Producto producto) {
        id = producto.getId();
        name = producto.getName();
        price = producto.getPrice();

        // Check if the usuario is not null before accessing its getId() method
        usuario = (producto.getUsuario() != null) ? producto.getUsuario().getId() : null;

    }
}
