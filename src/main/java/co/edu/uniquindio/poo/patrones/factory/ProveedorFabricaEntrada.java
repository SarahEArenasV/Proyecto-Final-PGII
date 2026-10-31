package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ProveedorFabricaEntrada {
    private final Map<TipoSala, FabricaEntrada> fabricas = new EnumMap<>(TipoSala.class);

    public ProveedorFabricaEntrada() {
        registrar(new FabricaEntradaGeneral());
        registrar(new FabricaEntrada3D());
        registrar(new FabricaEntradaVIP());
    }

    public final void registrar(FabricaEntrada fabrica) { fabricas.put(fabrica.getTipoSala(), fabrica); }

    public FabricaEntrada obtener(TipoSala tipo) {
        FabricaEntrada fabrica = fabricas.get(tipo);
        if (fabrica == null) throw new DatosInvalidosException("No hay fábrica registrada para " + tipo);
        return fabrica;
    }

    public Map<TipoSala, FabricaEntrada> getFabricas() { return Collections.unmodifiableMap(fabricas); }
}
