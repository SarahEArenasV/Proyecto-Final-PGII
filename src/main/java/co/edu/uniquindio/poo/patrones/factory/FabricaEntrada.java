package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.AsientoNoDisponibleException;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;

public abstract class FabricaEntrada {
    public Entrada crear(Funcion funcion, String codigoAsiento) {
        return crear(BaseDatos.getInstancia().siguienteId("ENT"), funcion, codigoAsiento);
    }

    public final Entrada crear(String id, Funcion funcion, String codigoAsiento) {
        if (funcion == null) throw new DatosInvalidosException("Debe seleccionar una función");
        if (funcion.getSala().getTipo() != getTipoSala())
            throw new DatosInvalidosException(getClass().getSimpleName() + " no corresponde a una sala " + funcion.getSala().getTipo());
        Asiento asiento = funcion.getSala().buscarAsiento(codigoAsiento);
        if (!asiento.isHabilitado())
            throw new AsientoNoDisponibleException("El asiento " + codigoAsiento + " no está habilitado");
        if (!funcion.isAsientoDisponible(codigoAsiento))
            throw new AsientoNoDisponibleException("El asiento " + codigoAsiento + " ya está ocupado");
        Entrada entrada = crearEntrada(id, funcion, asiento);
        validarEntrada(entrada);
        return entrada;
    }

    public abstract TipoSala getTipoSala();

    protected abstract Entrada crearEntrada(String id, Funcion funcion, Asiento asiento);

    protected void validarEntrada(Entrada entrada) {
        if (entrada.calcularPrecio() <= 0) throw new DatosInvalidosException("La entrada debe tener un precio válido");
    }
}
