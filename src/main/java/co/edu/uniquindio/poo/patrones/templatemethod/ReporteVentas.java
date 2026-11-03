package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteVentas extends ReporteTemplate {
    public ReporteVentas(FuenteDatosReporte datos) { super(datos); }

    private List<Compra> compras(LocalDate d, LocalDate h) {
        return datos.compras().stream().filter(c -> enRango(c.getFecha(), d, h))
                .filter(c -> !c.estaAnulada())
                .sorted(java.util.Comparator.comparing(Compra::getFecha)).toList();
    }

    @Override protected void validarPeriodo(LocalDate desde, LocalDate hasta) {
        super.validarPeriodo(desde, hasta);
        if (desde.plusYears(1).isBefore(hasta))
            throw new co.edu.uniquindio.poo.exceptions.DatosInvalidosException("El reporte de ventas admite máximo un año");
    }

    @Override protected String getTitulo() { return "Ventas realizadas por periodo"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Fecha", "Compra", "Cliente", "Película", "Entradas", "Confitería", "Descuento", "Total", "Pts usados", "Estado");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        List<List<String>> filas = new ArrayList<>();
        for (Compra c : compras(d, h)) {
            filas.add(List.of(Formato.fechaHora(c.getFecha()), c.getId(), c.getCliente().getNombre(),
                    c.getFuncion().getPelicula().getTitulo(), String.valueOf(c.getCantidadEntradas()),
                    Formato.moneda(c.getSubtotalConfiteria()), Formato.moneda(c.getDescuento()),
                    Formato.moneda(c.getTotal()), String.valueOf(c.getPuntosUsados()), c.getNombreEstado()));
        }
        return filas;
    }
    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        List<Compra> lista = compras(d, h);
        return List.of("Compras: " + lista.size(),
                "Entradas vendidas: " + lista.stream().mapToInt(Compra::getCantidadEntradas).sum(),
                "Descuentos otorgados: " + Formato.moneda(lista.stream().mapToDouble(Compra::getDescuento).sum()),
                "Total vendido: " + Formato.moneda(lista.stream().mapToDouble(Compra::getTotal).sum()));
    }
}
