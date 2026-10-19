package co.edu.uniquindio.poo.patrones.decorator;

import co.edu.uniquindio.poo.model.Promocion;

public class DescuentoEntradasDecorator extends PromocionDecorator {
    public DescuentoEntradasDecorator(PrecioCompra envuelto, Promocion promocion) { super(envuelto, promocion); }

    @Override public double getEntradas() { return envuelto.getEntradas() * factor(); }
}
