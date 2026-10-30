package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.EstadoFuncion;
import co.edu.uniquindio.poo.enums.TipoMovimiento;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.patrones.templatemethod.FuenteDatosReporte;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EstadisticaService {
    private final FuenteDatosReporte datos;

    public EstadisticaService(FuenteDatosReporte datos) { this.datos = datos; }

    private static void validarPeriodo(LocalDate d, LocalDate h) {
        if (d == null || h == null) throw new DatosInvalidosException("Seleccione la fecha inicial y la fecha final");
        if (h.isBefore(d)) throw new DatosInvalidosException("La fecha final no puede ser anterior a la inicial");
    }

    private List<Compra> vigentes(LocalDate d, LocalDate h) {
        return datos.compras().stream().filter(c -> c.estaPagada())
                .filter(c -> !c.getFecha().toLocalDate().isBefore(d) && !c.getFecha().toLocalDate().isAfter(h)).toList();
    }

    public Map<LocalDate, Double> ventasPorDia(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        Map<LocalDate, Double> mapa = new TreeMap<>();
        for (LocalDate f = d; !f.isAfter(h); f = f.plusDays(1)) mapa.put(f, 0.0);
        vigentes(d, h).forEach(c -> mapa.merge(c.getFecha().toLocalDate(), c.getTotal(), Double::sum));
        return mapa;
    }

    public Map<LocalDate, Double> recargasPorDia(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        Map<LocalDate, Double> mapa = new TreeMap<>();
        for (LocalDate f = d; !f.isAfter(h); f = f.plusDays(1)) mapa.put(f, 0.0);
        datos.clientes().forEach(c -> c.getTarjeta().getMovimientos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.RECARGA && mapa.containsKey(m.getFecha().toLocalDate()))
                .forEach(m -> mapa.merge(m.getFecha().toLocalDate(), m.getMonto(), Double::sum)));
        return mapa;
    }

    public Map<String, Integer> entradasPorPelicula(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        Map<String, Integer> mapa = new TreeMap<>();
        vigentes(d, h).forEach(c -> mapa.merge(c.getFuncion().getPelicula().getTitulo(), c.getCantidadEntradas(), Integer::sum));
        return ordenar(mapa);
    }

    public Map<String, Integer> productosMasVendidos(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        Map<String, Integer> mapa = new TreeMap<>();
        vigentes(d, h).stream().flatMap(c -> c.getConfiteria().stream())
                .forEach(l -> mapa.merge(l.getItem().getNombre(), l.getCantidad(), Integer::sum));
        return ordenar(mapa);
    }

    public static Map<String, Integer> top(Map<String, Integer> datos, int n) {
        Map<String, Integer> r = new LinkedHashMap<>();
        datos.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(n)
                .forEach(e -> r.put(e.getKey(), e.getValue()));
        return r;
    }

    public Map<String, Double> porcentajeVentasPorCategoria(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        Map<String, Double> ventas = ventasPorCategoria(d, h);
        double total = ventas.values().stream().mapToDouble(Double::doubleValue).sum();
        Map<String, Double> r = new LinkedHashMap<>();
        ventas.forEach((k, v) -> r.put(k, total == 0 ? 0 : v * 100 / total));
        return r;
    }

    public Map<String, Double> ventasPorCategoria(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        Map<String, Double> mapa = new LinkedHashMap<>();
        List<Compra> lista = vigentes(d, h);
        mapa.put("Entradas", lista.stream().mapToDouble(Compra::getSubtotalEntradas).sum());
        lista.stream().flatMap(c -> c.getConfiteria().stream()).filter(l -> !l.isRedimidaConPuntos())
                .forEach(l -> mapa.merge(l.getItem().getCategoria().toString(), l.getSubtotal(), Double::sum));
        return mapa;
    }

    public Map<String, Double> ocupacionPorSala(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        return datos.funciones().stream()
                .filter(f -> f.getEstado() != EstadoFuncion.CANCELADA && !f.getFecha().isBefore(d) && !f.getFecha().isAfter(h))
                .collect(Collectors.groupingBy(f -> f.getSala().getNombre(), TreeMap::new,
                        Collectors.averagingDouble(f -> f.getPorcentajeOcupacion())));
    }

    public double totalVentas(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        return vigentes(d, h).stream().mapToDouble(Compra::getTotal).sum();
    }
    public int totalEntradas(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        return vigentes(d, h).stream().mapToInt(Compra::getCantidadEntradas).sum();
    }
    public int totalProductos(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        return vigentes(d, h).stream().flatMap(c -> c.getConfiteria().stream()).mapToInt(LineaConfiteria::getCantidad).sum();
    }
    public double totalReembolsos(LocalDate d, LocalDate h) {
        validarPeriodo(d, h);
        return datos.compras().stream().filter(c -> c.getReembolso() != null)
                .filter(c -> !c.getReembolso().getFecha().toLocalDate().isBefore(d) && !c.getReembolso().getFecha().toLocalDate().isAfter(h))
                .mapToDouble(c -> c.getReembolso().getMontoReembolsado()).sum();
    }

    private static Map<String, Integer> ordenar(Map<String, Integer> mapa) {
        return mapa.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
}
