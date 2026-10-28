package co.edu.uniquindio.poo.patrones.builder;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;

import java.util.List;
import java.util.Set;

public class DirectorCompra {
    public ConstructorCompra construirSoloEntradas(ConstructorCompra builder, List<String> asientos) {
        requerirAsientos(asientos);
        builder.reiniciar();
        asientos.forEach(a -> builder.agregarEntrada(a, false));
        return builder;
    }

    public ConstructorCompra construirPersonalizada(ConstructorCompra builder, List<String> asientos,
                                                    Set<String> asientosConPuntos, List<LineaConfiteria> confiteria) {
        requerirAsientos(asientos);
        builder.reiniciar();
        asientos.forEach(a -> builder.agregarEntrada(a, asientosConPuntos.contains(a)));
        confiteria.forEach(l -> builder.agregarConfiteria(l.getItem(), l.getCantidad(), l.isRedimidaConPuntos()));
        return builder;
    }

    public ConstructorCompra construirPaqueteConCombo(ConstructorCompra builder, List<String> asientos, ItemConfiteria combo) {
        requerirAsientos(asientos);
        if (combo == null) throw new DatosInvalidosException("Seleccione el combo del paquete");
        builder.reiniciar();
        asientos.forEach(a -> builder.agregarEntrada(a, false));
        builder.agregarConfiteria(combo, (asientos.size() + 1) / 2, false);
        return builder;
    }

    public ConstructorCompra construirRedencionConPuntos(ConstructorCompra builder, List<String> asientos) {
        requerirAsientos(asientos);
        builder.reiniciar();
        asientos.forEach(a -> builder.agregarEntrada(a, true));
        return builder;
    }

    private static void requerirAsientos(List<String> asientos) {
        if (asientos == null || asientos.isEmpty()) throw new DatosInvalidosException("Debe seleccionar al menos un asiento");
    }
}
