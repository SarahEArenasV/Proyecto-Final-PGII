package co.edu.uniquindio.poo.patrones.decorator;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;

import java.util.List;

public class TopeDescuentoDecorator extends DecoradorPrecio {
    public static final double MAXIMO_PERMITIDO = 70;

    private final double porcentajeMaximo;

    public TopeDescuentoDecorator(PrecioCompra envuelto, double porcentajeMaximo) {
        super(envuelto);
        if (porcentajeMaximo < 0 || porcentajeMaximo > 100) throw new DatosInvalidosException("Tope de descuento inválido");
        this.porcentajeMaximo = porcentajeMaximo;
    }

    private double ajuste() {
        double totalEnvuelto = envuelto.getTotal();
        double minimo = getPrecioBase() * (1 - porcentajeMaximo / 100.0);
        return totalEnvuelto > 0 && totalEnvuelto < minimo ? minimo / totalEnvuelto : 1;
    }

    public boolean topeAplicado() { return ajuste() > 1; }

    @Override public double getEntradas() { return envuelto.getEntradas() * ajuste(); }
    @Override public double getConfiteria() { return envuelto.getConfiteria() * ajuste(); }

    @Override public List<String> getDetalle() {
        List<String> detalle = envuelto.getDetalle();
        if (topeAplicado())
            detalle.add(String.format("Tope de descuento %.0f %%: +$%,.0f", porcentajeMaximo, getTotal() - envuelto.getTotal()));
        return detalle;
    }
}
