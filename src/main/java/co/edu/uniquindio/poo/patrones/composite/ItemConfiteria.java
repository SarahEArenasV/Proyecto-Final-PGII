package co.edu.uniquindio.poo.patrones.composite;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.model.Identificable;

import java.util.List;
import java.util.Map;

public interface ItemConfiteria extends Identificable {
    String getNombre();
    double getPrecio();
    int getPuntosRedencion();
    CategoriaProducto getCategoria();
    boolean isActivo();
    String getDescripcion();

    List<ItemConfiteria> getComponentes();

    boolean contiene(ItemConfiteria item);

    int contarProductos();

    Map<Producto, Integer> getProductosIncluidos();

    default boolean esCompuesto() { return false; }
}
