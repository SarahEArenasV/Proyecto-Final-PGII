package co.edu.uniquindio.poo.enums;

import java.util.Arrays;

public enum CategoriaProducto {
    CRISPETAS, BEBIDA, DULCERIA, COMIDA, COMBO;

    public static CategoriaProducto[] deProductos() {
        return Arrays.stream(values()).filter(c -> c != COMBO).toArray(CategoriaProducto[]::new);
    }
}
