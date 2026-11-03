package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.enums.TipoMovimiento;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.MovimientoTarjeta;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReporteRecargas extends ReporteTemplate {
    public ReporteRecargas(FuenteDatosReporte datos) { super(datos); }

    private record Fila(Cliente cliente, MovimientoTarjeta mov) { }

    private List<Fila> recargas(LocalDate d, LocalDate h) {
        List<Fila> lista = new ArrayList<>();
        for (Cliente c : datos.clientes())
            for (MovimientoTarjeta m : c.getTarjeta().getMovimientos())
                if (m.getTipo() == TipoMovimiento.RECARGA && enRango(m.getFecha(), d, h)) lista.add(new Fila(c, m));
        lista.sort(Comparator.comparing(f -> f.mov().getFecha()));
        return lista;
    }

    @Override protected String getTitulo() { return "Recargas realizadas"; }
    @Override protected List<String> getEncabezados() {
        return List.of("Fecha", "Cliente", "Tarjeta", "Monto", "Saldo resultante", "Descripción");
    }
    @Override protected List<List<String>> obtenerFilas(LocalDate d, LocalDate h) {
        return recargas(d, h).stream().map(f -> List.of(Formato.fechaHora(f.mov().getFecha()), f.cliente().getNombre(),
                f.cliente().getTarjeta().getNumero(), Formato.moneda(f.mov().getMonto()),
                Formato.moneda(f.mov().getSaldoResultante()), f.mov().getDescripcion())).toList();
    }
    @Override protected List<String> calcularTotales(LocalDate d, LocalDate h) {
        List<Fila> lista = recargas(d, h);
        return List.of("Recargas: " + lista.size(),
                "Valor total recargado: " + Formato.moneda(lista.stream().mapToDouble(f -> f.mov().getMonto()).sum()));
    }
}
