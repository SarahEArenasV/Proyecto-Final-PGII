package co.edu.uniquindio.poo.patrones.composite;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class Producto extends ComponenteConfiteria {
    private double precio;
    private CategoriaProducto categoria;
    private int puntosRedencion;

    public Producto(String id, String nombre, double precio, CategoriaProducto categoria, int puntosRedencion) {
        super(id, nombre);
        setPrecio(precio);
        setCategoria(categoria);
        setPuntosRedencion(puntosRedencion);
    }

    @Override public boolean isActivo() { return isActivoPropio(); }
    @Override public String getDescripcion() { return getNombre(); }

    @Override public List<ItemConfiteria> getComponentes() { return List.of(); }
    @Override public boolean contiene(ItemConfiteria item) { return equals(item); }
    @Override public int contarProductos() { return 1; }
    @Override public Map<Producto, Integer> getProductosIncluidos() { return Map.of(this, 1); }

    public void setPrecio(double precio) {
        if (precio <= 0) throw new DatosInvalidosException("El precio debe ser mayor que cero");
        this.precio = precio;
    }

    public void setCategoria(CategoriaProducto categoria) {
        if (categoria == null || categoria == CategoriaProducto.COMBO)
            throw new DatosInvalidosException("Seleccione una categoría válida");
        this.categoria = categoria;
    }

    public void setPuntosRedencion(int puntosRedencion) {
        if (puntosRedencion <= 0) throw new DatosInvalidosException("Los puntos de redención deben ser mayores que cero");
        this.puntosRedencion = puntosRedencion;
    }
}
