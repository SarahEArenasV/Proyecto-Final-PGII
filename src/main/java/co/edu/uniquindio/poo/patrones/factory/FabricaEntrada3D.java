package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;

public class FabricaEntrada3D extends FabricaEntrada {
    @Override public TipoSala getTipoSala() { return TipoSala.SALA_3D; }

    @Override protected Entrada crearEntrada(String id, Funcion funcion, Asiento asiento) {
        return new Entrada3D(id, funcion, asiento);
    }
}
