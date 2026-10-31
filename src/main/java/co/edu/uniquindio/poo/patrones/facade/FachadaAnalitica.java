package co.edu.uniquindio.poo.patrones.facade;

import co.edu.uniquindio.poo.enums.FormatoReporte;
import co.edu.uniquindio.poo.enums.TipoReporte;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.servicios.EstadisticaService;
import co.edu.uniquindio.poo.servicios.ReporteService;
import co.edu.uniquindio.poo.patrones.templatemethod.FuenteDatosReporte;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class FachadaAnalitica {
    private final ReporteService reportes;
    private final EstadisticaService estadisticas;

    public FachadaAnalitica(FuenteDatosReporte fuente) {
        this.reportes = new ReporteService(fuente);
        this.estadisticas = new EstadisticaService(fuente);
    }

    public Reporte generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) { return reportes.generar(tipo, desde, hasta); }

    public void exportarReporte(Reporte reporte, FormatoReporte formato, File destino) throws IOException {
        reportes.exportar(reporte, formato, destino);
    }

    public Map<LocalDate, Double> ventasPorDia(LocalDate d, LocalDate h) { return estadisticas.ventasPorDia(d, h); }
    public Map<LocalDate, Double> recargasPorDia(LocalDate d, LocalDate h) { return estadisticas.recargasPorDia(d, h); }
    public Map<String, Integer> entradasPorPelicula(LocalDate d, LocalDate h) { return estadisticas.entradasPorPelicula(d, h); }
    public Map<String, Integer> productosMasVendidos(LocalDate d, LocalDate h) { return estadisticas.productosMasVendidos(d, h); }
    public Map<String, Double> ventasPorCategoria(LocalDate d, LocalDate h) { return estadisticas.ventasPorCategoria(d, h); }
    public Map<String, Double> ocupacionPorSala(LocalDate d, LocalDate h) { return estadisticas.ocupacionPorSala(d, h); }
    public double totalVentas(LocalDate d, LocalDate h) { return estadisticas.totalVentas(d, h); }
    public int totalEntradas(LocalDate d, LocalDate h) { return estadisticas.totalEntradas(d, h); }
    public int totalProductos(LocalDate d, LocalDate h) { return estadisticas.totalProductos(d, h); }
    public double totalReembolsos(LocalDate d, LocalDate h) { return estadisticas.totalReembolsos(d, h); }

    public Map<String, Integer> topEntradasPorPelicula(LocalDate d, LocalDate h, int n) {
        return EstadisticaService.top(estadisticas.entradasPorPelicula(d, h), n);
    }
    public Map<String, Integer> topProductos(LocalDate d, LocalDate h, int n) {
        return EstadisticaService.top(estadisticas.productosMasVendidos(d, h), n);
    }
    public Map<String, Double> porcentajeVentasPorCategoria(LocalDate d, LocalDate h) {
        return estadisticas.porcentajeVentasPorCategoria(d, h);
    }

    public Map<String, Object> indicadores(LocalDate d, LocalDate h) {
        Map<String, Object> kpi = new LinkedHashMap<>();
        kpi.put("ventas", totalVentas(d, h));
        kpi.put("entradas", totalEntradas(d, h));
        kpi.put("productos", totalProductos(d, h));
        kpi.put("reembolsos", totalReembolsos(d, h));
        return kpi;
    }
}
