package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Reporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public abstract class ReporteTemplate {
    protected final FuenteDatosReporte datos;

    protected ReporteTemplate(FuenteDatosReporte datos) { this.datos = datos; }

    public final Reporte generar(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        List<List<String>> filas = ordenarFilas(obtenerFilas(desde, hasta));
        List<String> totales = incluirTotales() ? calcularTotales(desde, hasta) : List.of();
        return new Reporte(getTitulo(), LocalDateTime.now(), desde, hasta, getEncabezados(), filas, totales);
    }

    protected List<List<String>> ordenarFilas(List<List<String>> filas) { return filas; }

    protected boolean incluirTotales() { return true; }

    protected void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) throw new DatosInvalidosException("Debe indicar el periodo del reporte");
        if (desde.isAfter(hasta)) throw new DatosInvalidosException("La fecha inicial no puede ser mayor a la final");
    }

    protected abstract String getTitulo();
    protected abstract List<String> getEncabezados();
    protected abstract List<List<String>> obtenerFilas(LocalDate desde, LocalDate hasta);
    protected abstract List<String> calcularTotales(LocalDate desde, LocalDate hasta);

    protected static boolean enRango(LocalDateTime fecha, LocalDate desde, LocalDate hasta) {
        LocalDate d = fecha.toLocalDate();
        return !d.isBefore(desde) && !d.isAfter(hasta);
    }

    protected static boolean enRango(LocalDate d, LocalDate desde, LocalDate hasta) {
        return !d.isBefore(desde) && !d.isAfter(hasta);
    }
}
