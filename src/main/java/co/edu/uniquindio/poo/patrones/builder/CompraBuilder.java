package co.edu.uniquindio.poo.patrones.builder;

import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.patrones.factory.ProveedorFabricaEntrada;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;

public class CompraBuilder extends ConstructorCompraBase {
    public CompraBuilder(ProveedorFabricaEntrada proveedorFabricas) { super(proveedorFabricas); }

    public Compra getResultado() {
        validar();
        Calculo c = calcular(true);
        return new Compra(BaseDatos.getInstancia().siguienteId("CMP"), cliente, funcion, c.entradas(), confiteria,
                c.subtotalEntradas(), c.subtotalConfiteria(), c.total(), c.puntos(), c.precio().getDetalle(), fecha);
    }
}
