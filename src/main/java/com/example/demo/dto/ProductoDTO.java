package com.example.demo.dto;

import com.example.demo.modelo.Producto;
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
    Long usuarioId;

    public ProductoDTO(Producto producto){
        id = producto.getId();
        name = producto.getName();
        price = producto.getPrice();
        usuarioId = producto.getUsuario().getId();
    }
}
