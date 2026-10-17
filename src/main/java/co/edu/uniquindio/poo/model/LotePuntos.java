package co.edu.uniquindio.poo.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LotePuntos {
    private final String id;
    private final int cantidadInicial;
    private int disponibles;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private final String origen;

    public LotePuntos(String id, int cantidad, LocalDate fechaObtencion, String origen) {
        this.id = id;
        this.cantidadInicial = cantidad;
        this.disponibles = cantidad;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
        this.origen = origen;
    }

    public boolean estaVigente(LocalDate hoy) { return !hoy.isAfter(fechaVencimiento); }

    public int consumir(int cantidad) {
        int usados = Math.min(cantidad, disponibles);
        disponibles -= usados;
        return usados;
    }

    public void devolver(int cantidad) { disponibles = Math.min(cantidadInicial, disponibles + cantidad); }
}
