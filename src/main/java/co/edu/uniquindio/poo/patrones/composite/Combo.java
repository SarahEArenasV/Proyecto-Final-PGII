package co.edu.uniquindio.poo.patrones.composite;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Combo extends ComponenteConfiteria {
    public static final double FACTOR_PUNTOS = 0.8;

    @Getter private double porcentajeDescuento;
    private final List<ItemConfiteria> componentes = new ArrayList<>();

    public Combo(String id, String nombre, double porcentajeDescuento) {
        super(id, nombre);
        setPorcentajeDescuento(porcentajeDescuento);
    }

    public Combo agregar(ItemConfiteria item) {
        if (item == null) throw new DatosInvalidosException("El componente es obligatorio");
        if (item.contiene(this)) throw new DatosInvalidosException("Un combo no puede contenerse a sí mismo");
        componentes.add(item);
        return this;
    }

    public void remover(ItemConfiteria item) { componentes.remove(item); }
    public void limpiar() { componentes.clear(); }

    @Override public double getPrecio() {
        double suma = componentes.stream().mapToDouble(ItemConfiteria::getPrecio).sum();
        return Math.round(suma * (1 - porcentajeDescuento / 100.0));
    }

    @Override public int getPuntosRedencion() {
        return (int) Math.round(componentes.stream().mapToInt(ItemConfiteria::getPuntosRedencion).sum() * FACTOR_PUNTOS);
    }

    @Override public boolean isActivo() {
        return isActivoPropio() && !componentes.isEmpty() && componentes.stream().allMatch(ItemConfiteria::isActivo);
    }

    @Override public String getDescripcion() {
        return getNombre() + " [" + componentes.stream().map(ItemConfiteria::getNombre).collect(Collectors.joining(", ")) + "]";
    }

    @Override public boolean contiene(ItemConfiteria item) {
        return equals(item) || componentes.stream().anyMatch(c -> c.contiene(item));
    }

    @Override public int contarProductos() {
        return componentes.stream().mapToInt(ItemConfiteria::contarProductos).sum();
    }

    @Override public Map<Producto, Integer> getProductosIncluidos() {
        Map<Producto, Integer> total = new LinkedHashMap<>();
        componentes.forEach(c -> c.getProductosIncluidos().forEach((p, n) -> total.merge(p, n, Integer::sum)));
        return total;
    }

    public double getAhorro() {
        return componentes.stream().mapToDouble(ItemConfiteria::getPrecio).sum() - getPrecio();
    }

    @Override public boolean esCompuesto() { return true; }
    @Override public List<ItemConfiteria> getComponentes() { return Collections.unmodifiableList(componentes); }
    @Override public CategoriaProducto getCategoria() { return CategoriaProducto.COMBO; }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento >= 100)
            throw new DatosInvalidosException("Descuento del combo entre 0 y 99 %");
        this.porcentajeDescuento = porcentajeDescuento;
    }
}
