package co.edu.uniquindio.poo.patrones.decorator;

import co.edu.uniquindio.poo.model.Promocion;

public class DescuentoTotalDecorator extends PromocionDecorator {
    public DescuentoTotalDecorator(PrecioCompra envuelto, Promocion promocion) { super(envuelto, promocion); }

    @Override public double getEntradas() { return envuelto.getEntradas() * factor(); }
    @Override public double getConfiteria() { return envuelto.getConfiteria() * factor(); }
}
