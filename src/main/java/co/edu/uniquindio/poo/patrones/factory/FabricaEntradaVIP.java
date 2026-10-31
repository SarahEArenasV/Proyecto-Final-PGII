package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;

public class FabricaEntradaVIP extends FabricaEntrada {
    @Override public TipoSala getTipoSala() { return TipoSala.VIP; }

    @Override protected Entrada crearEntrada(String id, Funcion funcion, Asiento asiento) {
        return new EntradaVIP(id, funcion, asiento);
    }
}
