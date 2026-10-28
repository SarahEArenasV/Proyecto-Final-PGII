package co.edu.uniquindio.poo.patrones.builder;

import co.edu.uniquindio.poo.patrones.factory.ProveedorFabricaEntrada;

public class ResumenCompraBuilder extends ConstructorCompraBase {
    public ResumenCompraBuilder(ProveedorFabricaEntrada proveedorFabricas) { super(proveedorFabricas); }

    public ResumenCompra getResultado() {
        validar();
        Calculo c = calcular(false);
        return new ResumenCompra(c.subtotalEntradas(), c.subtotalConfiteria(), c.total(), c.puntos(),
                c.precio().getDetalle(), asientos.size(), confiteria.size());
    }
}
