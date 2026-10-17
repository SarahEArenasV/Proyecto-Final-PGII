package co.edu.uniquindio.poo.patrones.decorator;

import java.util.List;

public interface PrecioCompra {
    double getEntradas();
    double getConfiteria();
    List<String> getDetalle();

    double getPrecioBase();

    default double getTotal() { return Math.max(0, getEntradas() + getConfiteria()); }
    default double getDescuento() { return Math.max(0, getPrecioBase() - getTotal()); }
}
