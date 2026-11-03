package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReporteProductos extends ReporteTemplate {
    public ReporteProductos(FuenteDatosReporte datos) { super(datos); }

    private List<LineaConfiteria> lineas(LocalDate d, LocalDate h) {
        return datos.compras().stream().filter(c -> enRango(c.getFecha(), d, h))
                .filter(c -> c.estaPagada())
                .flatMap(c -> c.getConfiteria().stream()).toList();
    }

    @Override protected String getTitulo() { return "Productos y combos vendidos"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Producto / Combo", "Categoría", "Cantidad", "Redimidos con puntos", "Ingresos");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        Map<String, List<LineaConfiteria>> porItem = lineas(d, h).stream()
                .collect(Collectors.groupingBy(l -> l.getItem().getNombre()));
        List<List<String>> filas = new ArrayList<>();
        porItem.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, List<LineaConfiteria>> e) ->
                        e.getValue().stream().mapToInt(LineaConfiteria::getCantidad).sum()).reversed())
                .forEach(e -> {
                    List<LineaConfiteria> ls = e.getValue();
                    filas.add(List.of(e.getKey(), ls.get(0).getItem().getCategoria().toString(),
                            String.valueOf(ls.stream().mapToInt(LineaConfiteria::getCantidad).sum()),
                            String.valueOf(ls.stream().filter(LineaConfiteria::isRedimidaConPuntos).mapToInt(LineaConfiteria::getCantidad).sum()),
                            Formato.moneda(ls.stream().filter(l -> !l.isRedimidaConPuntos()).mapToDouble(LineaConfiteria::getSubtotal).sum())));
                });
        return filas;
    }
    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        List<LineaConfiteria> ls = lineas(d, h);
        return List.of("Unidades vendidas: " + ls.stream().mapToInt(LineaConfiteria::getCantidad).sum(),
                "Ingresos confitería (antes de promociones): " + Formato.moneda(
                        ls.stream().filter(l -> !l.isRedimidaConPuntos()).mapToDouble(LineaConfiteria::getSubtotal).sum()));
    }
}
