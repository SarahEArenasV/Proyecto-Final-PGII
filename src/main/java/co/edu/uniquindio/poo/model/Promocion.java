package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoPromocion;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Getter
@Setter
public class Promocion implements Identificable {
    private final String id;
    private String nombre;
    private String descripcion;
    private double porcentaje;
    private TipoPromocion tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private DayOfWeek diaAplicable;
    private boolean activa = true;

    public Promocion(String id, String nombre, String descripcion, double porcentaje, TipoPromocion tipo,
                     LocalDate fechaInicio, LocalDate fechaFin, DayOfWeek diaAplicable) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.porcentaje = porcentaje;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diaAplicable = diaAplicable;
    }

    public boolean aplicaPara(LocalDate fechaFuncion) {
        if (!activa) return false;
        if (fechaFuncion.isBefore(fechaInicio) || fechaFuncion.isAfter(fechaFin)) return false;
        return diaAplicable == null || fechaFuncion.getDayOfWeek() == diaAplicable;
    }

    @Override
    public String toString() {
        return nombre + " (" + porcentaje + "% " + tipo + ")";
    }
}
