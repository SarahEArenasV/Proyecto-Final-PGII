package co.edu.uniquindio.poo.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Reembolso {
    private final double montoPagado;
    private final double porcentaje;
    private final double montoReembolsado;
    private final int puntosDevueltos;
    private final String motivo;
    private final LocalDateTime fecha;

    public Reembolso(double montoPagado, double porcentaje, int puntosDevueltos, String motivo, LocalDateTime fecha) {
        this.montoPagado = montoPagado;
        this.porcentaje = porcentaje;
        this.montoReembolsado = Math.round(montoPagado * porcentaje / 100.0);
        this.puntosDevueltos = puntosDevueltos;
        this.motivo = motivo;
        this.fecha = fecha;
    }
}
