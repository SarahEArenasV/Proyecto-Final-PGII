package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.enums.TipoMovimientoPuntos;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.MovimientoPuntos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportePuntos extends ReporteTemplate {
    public ReportePuntos(FuenteDatosReporte datos) { super(datos); }

    private int sumar(Cliente c, TipoMovimientoPuntos tipo, LocalDate d, LocalDate h) {
        return c.getCuentaPuntos().getMovimientos().stream()
                .filter(m -> m.getTipo() == tipo && enRango(m.getFecha(), d, h))
                .mapToInt(MovimientoPuntos::getCantidad).sum();
    }

    @Override protected String getTitulo() { return "Puntos acumulados y redimidos"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Cliente", "Documento", "Acumulados", "Redimidos", "Devueltos", "Revertidos", "Disponibles hoy", "Vencidos");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        LocalDate hoy = LocalDate.now();
        List<List<String>> filas = new ArrayList<>();
        for (Cliente c : datos.clientes()) {
            filas.add(List.of(c.getNombre(), c.getDocumento(),
                    String.valueOf(sumar(c, TipoMovimientoPuntos.ACUMULACION, d, h)),
                    String.valueOf(sumar(c, TipoMovimientoPuntos.REDENCION, d, h)),
                    String.valueOf(sumar(c, TipoMovimientoPuntos.DEVOLUCION, d, h)),
                    String.valueOf(sumar(c, TipoMovimientoPuntos.REVERSION, d, h)),
                    String.valueOf(c.getCuentaPuntos().getPuntosDisponibles(hoy)),
                    String.valueOf(c.getCuentaPuntos().getPuntosVencidos(hoy))));
        }
        return filas;
    }
    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        int acum = 0, red = 0, disp = 0;
        for (Cliente c : datos.clientes()) {
            acum += sumar(c, TipoMovimientoPuntos.ACUMULACION, d, h);
            red += sumar(c, TipoMovimientoPuntos.REDENCION, d, h);
            disp += c.getCuentaPuntos().getPuntosDisponibles(LocalDate.now());
        }
        return List.of("Puntos acumulados: " + acum, "Puntos redimidos: " + red, "Puntos disponibles (todos los clientes): " + disp);
    }
}
