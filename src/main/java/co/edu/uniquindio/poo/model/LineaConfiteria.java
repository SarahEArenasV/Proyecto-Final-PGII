package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import lombok.Getter;

@Getter
public class LineaConfiteria {
    private final ItemConfiteria item;
    private final int cantidad;
    private final boolean redimidaConPuntos;

    public LineaConfiteria(ItemConfiteria item, int cantidad, boolean redimidaConPuntos) {
        this.item = item;
        this.cantidad = cantidad;
        this.redimidaConPuntos = redimidaConPuntos;
    }

    public double getSubtotal() { return item.getPrecio() * cantidad; }
    public int getPuntosRequeridos() { return item.getPuntosRedencion() * cantidad; }

    @Override public String toString() {
        return cantidad + " x " + item.getNombre() + (redimidaConPuntos ? " (puntos)" : "");
    }
}
