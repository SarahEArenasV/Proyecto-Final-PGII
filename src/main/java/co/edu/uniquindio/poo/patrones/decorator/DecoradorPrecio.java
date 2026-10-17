package co.edu.uniquindio.poo.patrones.decorator;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class DecoradorPrecio implements PrecioCompra {
    protected final PrecioCompra envuelto;

    protected DecoradorPrecio(PrecioCompra envuelto) {
        this.envuelto = Objects.requireNonNull(envuelto, "El precio a decorar es obligatorio");
    }

    @Override public double getEntradas() { return envuelto.getEntradas(); }
    @Override public double getConfiteria() { return envuelto.getConfiteria(); }
    @Override public double getPrecioBase() { return envuelto.getPrecioBase(); }
    @Override public List<String> getDetalle() { return envuelto.getDetalle(); }
}
