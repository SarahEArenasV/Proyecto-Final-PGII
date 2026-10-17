package co.edu.uniquindio.poo.patrones.decorator;

import co.edu.uniquindio.poo.model.Promocion;

public class DescuentoConfiteriaDecorator extends PromocionDecorator {
    public DescuentoConfiteriaDecorator(PrecioCompra envuelto, Promocion promocion) { super(envuelto, promocion); }

    @Override public double getConfiteria() { return envuelto.getConfiteria() * factor(); }
}
