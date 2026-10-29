package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Reporte(String titulo, LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                      List<String> encabezados, List<List<String>> filas, List<String> totales) {
    public int getCantidadRegistros() { return filas.size(); }
}
