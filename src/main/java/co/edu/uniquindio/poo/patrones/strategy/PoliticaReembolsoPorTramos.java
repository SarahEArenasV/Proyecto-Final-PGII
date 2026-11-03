package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.exceptions.CancelacionNoPermitidaException;
import co.edu.uniquindio.poo.model.Compra;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class PoliticaReembolsoPorTramos implements PoliticaReembolso {
    public record Tramo(long minutosMinimos, boolean inclusivo, double porcentaje) {
        boolean aplica(long minutosRestantes) {
            return inclusivo ? minutosRestantes >= minutosMinimos : minutosRestantes > minutosMinimos;
        }
    }

    protected abstract List<Tramo> getTramos();

    @Override public final double calcularPorcentaje(Compra compra, LocalDateTime ahora) {
        long minutos = Duration.between(ahora, compra.getFuncion().getFechaHora()).toMinutes();
        return getTramos().stream().filter(t -> t.aplica(minutos)).findFirst()
                .map(Tramo::porcentaje)
                .orElseThrow(() -> new CancelacionNoPermitidaException(mensajeNoPermitido()));
    }

    protected abstract String mensajeNoPermitido();
}
