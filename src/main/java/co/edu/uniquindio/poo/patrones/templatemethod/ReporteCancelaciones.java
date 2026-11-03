package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDate;
import java.util.List;

public class ReporteCancelaciones extends ReporteTemplate {
    public ReporteCancelaciones(FuenteDatosReporte datos) { super(datos); }

    private List<Compra> canceladas(LocalDate d, LocalDate h) {
        return datos.compras().stream().filter(c -> c.getReembolso() != null)
                .filter(c -> enRango(c.getReembolso().getFecha(), d, h)).toList();
    }

    @Override protected String getTitulo() { return "Cancelaciones y reembolsos"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Fecha", "Compra", "Cliente", "Película", "Motivo", "Pagado", "%", "Reembolsado", "Pts devueltos");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        return canceladas(d, h).stream().map(c -> List.of(Formato.fechaHora(c.getReembolso().getFecha()), c.getId(),
                c.getCliente().getNombre(), c.getFuncion().getPelicula().getTitulo(), c.getReembolso().getMotivo(),
                Formato.moneda(c.getReembolso().getMontoPagado()), String.format("%.0f%%", c.getReembolso().getPorcentaje()),
                Formato.moneda(c.getReembolso().getMontoReembolsado()), String.valueOf(c.getReembolso().getPuntosDevueltos()))).toList();
    }
    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        List<Compra> lista = canceladas(d, h);
        return List.of("Cancelaciones: " + lista.size(),
                "Cancelaciones por el cine: " + lista.stream().filter(c -> c.getReembolso().getMotivo().contains("cine")).count(),
                "Total reembolsado: " + Formato.moneda(lista.stream().mapToDouble(c -> c.getReembolso().getMontoReembolsado()).sum()),
                "Puntos devueltos: " + lista.stream().mapToInt(c -> c.getReembolso().getPuntosDevueltos()).sum());
    }
}
