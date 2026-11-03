package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;

import java.time.LocalDateTime;

public class PoliticaReembolsoCine implements PoliticaReembolso {
    @Override public double calcularPorcentaje(Compra compra, LocalDateTime ahora) { return 100; }
    @Override public String getDescripcion() { return "Función cancelada por el cine"; }
}
