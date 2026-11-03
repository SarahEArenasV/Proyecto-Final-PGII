package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.enums.EstadoFuncion;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Sala;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteOcupacion extends ReporteTemplate {
    public ReporteOcupacion(FuenteDatosReporte datos) { super(datos); }

    private Map<Sala, List<Funcion>> agrupar(LocalDate d, LocalDate h) {
        Map<Sala, List<Funcion>> mapa = new LinkedHashMap<>();
        datos.funciones().stream().filter(f -> enRango(f.getFecha(), d, h))
                .filter(f -> f.getEstado() != EstadoFuncion.CANCELADA)
                .forEach(f -> mapa.computeIfAbsent(f.getSala(), k -> new ArrayList<>()).add(f));
        return mapa;
    }

    @Override protected String getTitulo() { return "Ocupación de las salas"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Sala", "Tipo", "Funciones", "Capacidad ofrecida", "Asientos vendidos", "% Ocupación");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        List<List<String>> filas = new ArrayList<>();
        agrupar(d, h).forEach((sala, funciones) -> {
            int capacidad = sala.getCapacidad() * funciones.size();
            int vendidos = funciones.stream().mapToInt(f -> f.getAsientosOcupados().size()).sum();
            filas.add(List.of(sala.getNombre(), sala.getTipo().toString(), String.valueOf(funciones.size()),
                    String.valueOf(capacidad), String.valueOf(vendidos),
                    String.format("%.1f %%", capacidad == 0 ? 0 : vendidos * 100.0 / capacidad)));
        });
        return filas;
    }

    @Override protected List<List<String>> ordenarFilas(List<List<String>> filas) {
        List<List<String>> ordenadas = new ArrayList<>(filas);
        ordenadas.sort((a, b) -> Double.compare(porcentaje(b), porcentaje(a)));
        return ordenadas;
    }

    private static double porcentaje(List<String> fila) {
        return Double.parseDouble(fila.get(5).replace("%", "").replace(",", ".").trim());
    }

    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        int capacidad = 0, vendidos = 0, funciones = 0;
        for (Map.Entry<Sala, List<Funcion>> e : agrupar(d, h).entrySet()) {
            funciones += e.getValue().size();
            capacidad += e.getKey().getCapacidad() * e.getValue().size();
            vendidos += e.getValue().stream().mapToInt(f -> f.getAsientosOcupados().size()).sum();
        }
        return List.of("Funciones: " + funciones, "Capacidad total: " + capacidad, "Asientos vendidos: " + vendidos,
                String.format("Ocupación promedio: %.1f %%", capacidad == 0 ? 0 : vendidos * 100.0 / capacidad));
    }
}
