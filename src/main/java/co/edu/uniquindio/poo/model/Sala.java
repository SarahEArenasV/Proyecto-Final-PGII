package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoAsiento;
import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.exceptions.EntidadNoEncontradaException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala implements Identificable {
    @Getter private final String id;
    @Getter @Setter private String nombre;
    @Getter @Setter private TipoSala tipo;
    @Getter private int filas;
    @Getter private int columnas;
    private final List<Asiento> asientos = new ArrayList<>();

    public Sala(String id, String nombre, TipoSala tipo, int filas, int columnas) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        generarAsientos(filas, columnas);
    }

    public final void generarAsientos(int filas, int columnas) {
        if (filas < 1 || filas > 26 || columnas < 1 || columnas > 30) {
            throw new DatosInvalidosException("Filas entre 1 y 26, columnas entre 1 y 30");
        }
        this.filas = filas;
        this.columnas = columnas;
        asientos.clear();
        for (int f = 0; f < filas; f++) {
            char letra = (char) ('A' + f);
            for (int c = 1; c <= columnas; c++) {
                TipoAsiento tipoAsiento = TipoAsiento.ESTANDAR;
                if (f >= filas - 2 && filas > 3) tipoAsiento = TipoAsiento.PREFERENCIAL;
                if (f == 0 && (c == 1 || c == columnas)) tipoAsiento = TipoAsiento.DISCAPACIDAD;
                asientos.add(new Asiento(letra, c, tipoAsiento));
            }
        }
    }

    public Asiento buscarAsiento(String codigo) {
        return asientos.stream().filter(a -> a.getCodigo().equalsIgnoreCase(codigo)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Asiento " + codigo + " no existe en la sala"));
    }

    public int getCapacidad() { return (int) asientos.stream().filter(Asiento::isHabilitado).count(); }

    public List<Asiento> getAsientos() { return Collections.unmodifiableList(asientos); }

    @Override public String toString() { return nombre + " - " + tipo; }
}
