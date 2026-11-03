package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.patrones.factory.Entrada;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReporteEntradas extends ReporteTemplate {
    public ReporteEntradas(FuenteDatosReporte datos) { super(datos); }

    private List<Entrada> entradas(LocalDate d, LocalDate h) {
        return datos.compras().stream().filter(c -> enRango(c.getFecha(), d, h))
                .filter(c -> c.estaPagada())
                .flatMap(c -> c.getEntradas().stream()).toList();
    }

    @Override protected String getTitulo() { return "Entradas vendidas por película y función"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Película", "Función", "Sala", "Entradas", "Con puntos", "Recaudo");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        Map<Funcion, List<Entrada>> porFuncion = new LinkedHashMap<>();
        entradas(d, h).stream().sorted(Comparator.comparing((Entrada e) -> e.getFuncion().getPelicula().getTitulo())
                        .thenComparing(e -> e.getFuncion().getFechaHora()))
                .forEach(e -> porFuncion.computeIfAbsent(e.getFuncion(), k -> new ArrayList<>()).add(e));
        List<List<String>> filas = new ArrayList<>();
        porFuncion.forEach((f, lista) -> filas.add(List.of(f.getPelicula().getTitulo(),
                Formato.fechaHora(f.getFechaHora()), f.getSala().getNombre(), String.valueOf(lista.size()),
                String.valueOf(lista.stream().filter(Entrada::isRedimidaConPuntos).count()),
                Formato.moneda(lista.stream().filter(e -> !e.isRedimidaConPuntos()).mapToDouble(Entrada::calcularPrecio).sum()))));
        return filas;
    }
    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        List<Entrada> lista = entradas(d, h);
        Map<String, Long> porPelicula = new TreeMap<>();
        lista.forEach(e -> porPelicula.merge(e.getFuncion().getPelicula().getTitulo(), 1L, Long::sum));
        List<String> totales = new ArrayList<>();
        porPelicula.forEach((p, n) -> totales.add(p + ": " + n + " entradas"));
        totales.add("Total entradas: " + lista.size());
        totales.add("Recaudo total entradas: " + Formato.moneda(
                lista.stream().filter(e -> !e.isRedimidaConPuntos()).mapToDouble(Entrada::calcularPrecio).sum()));
        return totales;
    }
}
