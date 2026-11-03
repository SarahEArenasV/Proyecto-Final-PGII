package co.edu.uniquindio.poo.patrones.strategy;

import java.util.List;

public class PoliticaReembolsoCliente extends PoliticaReembolsoPorTramos {
    private static final List<Tramo> TRAMOS = List.of(
            new Tramo(24 * 60, false, 100),
            new Tramo(2 * 60, true, 80));

    @Override protected List<Tramo> getTramos() { return TRAMOS; }
    @Override protected String mensajeNoPermitido() { return "No se permite cancelar con menos de 2 horas de anticipación"; }
    @Override public String getDescripcion() { return "Cancelación solicitada por el cliente"; }
}
