package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;

import java.time.LocalDateTime;

public interface PoliticaReembolso {
    double calcularPorcentaje(Compra compra, LocalDateTime ahora);
    String getDescripcion();
}
