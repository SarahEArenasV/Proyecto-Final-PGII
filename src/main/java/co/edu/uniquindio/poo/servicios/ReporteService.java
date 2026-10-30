package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.FormatoReporte;
import co.edu.uniquindio.poo.enums.TipoReporte;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.patrones.templatemethod.FuenteDatosReporte;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteCancelaciones;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteEntradas;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteOcupacion;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteProductos;
import co.edu.uniquindio.poo.patrones.templatemethod.ReportePuntos;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteRecargas;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteTemplate;
import co.edu.uniquindio.poo.patrones.templatemethod.ReporteVentas;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

public class ReporteService {
    private final Map<TipoReporte, ReporteTemplate> reportes = new EnumMap<>(TipoReporte.class);
    private final ExportadorReportes exportador = new ExportadorReportes();

    public ReporteService(FuenteDatosReporte datos) {
        reportes.put(TipoReporte.VENTAS_PERIODO, new ReporteVentas(datos));
        reportes.put(TipoReporte.ENTRADAS_POR_PELICULA, new ReporteEntradas(datos));
        reportes.put(TipoReporte.OCUPACION_SALAS, new ReporteOcupacion(datos));
        reportes.put(TipoReporte.PRODUCTOS_VENDIDOS, new ReporteProductos(datos));
        reportes.put(TipoReporte.RECARGAS, new ReporteRecargas(datos));
        reportes.put(TipoReporte.CANCELACIONES, new ReporteCancelaciones(datos));
        reportes.put(TipoReporte.PUNTOS, new ReportePuntos(datos));
    }

    public Reporte generar(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        if (tipo == null) throw new DatosInvalidosException("Seleccione el tipo de reporte");
        return reportes.get(tipo).generar(desde, hasta);
    }

    public void exportar(Reporte reporte, FormatoReporte formato, File destino) throws IOException {
        exportador.exportar(reporte, formato, destino);
    }
}
