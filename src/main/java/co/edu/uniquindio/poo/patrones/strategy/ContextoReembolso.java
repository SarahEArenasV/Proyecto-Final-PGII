package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class ContextoReembolso {
    private PoliticaReembolso politica;

    public ContextoReembolso(PoliticaReembolso politica) { setPolitica(politica); }

    public void setPolitica(PoliticaReembolso politica) {
        this.politica = Objects.requireNonNull(politica, "La política de reembolso es obligatoria");
    }

    public double calcularPorcentaje(Compra compra, LocalDateTime ahora) { return politica.calcularPorcentaje(compra, ahora); }

    public double calcularMonto(Compra compra, LocalDateTime ahora) {
        return Math.round(compra.getTotal() * calcularPorcentaje(compra, ahora) / 100.0);
    }

    public String getDescripcion() { return politica.getDescripcion(); }
}
