package co.edu.uniquindio.poo.patrones.builder;

import java.util.List;

public record ResumenCompra(double subtotalEntradas, double subtotalConfiteria, double total, int puntosRequeridos,
                            List<String> promocionesAplicadas, int cantidadEntradas, int lineasConfiteria) {
    public double descuento() { return Math.max(0, subtotalEntradas + subtotalConfiteria - total); }
}
