package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.model.LotePuntos;
import co.edu.uniquindio.poo.model.MovimientoPuntos;
import co.edu.uniquindio.poo.model.MovimientoTarjeta;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.model.Reembolso;
import co.edu.uniquindio.poo.model.SolicitudRecarga;
import co.edu.uniquindio.poo.patrones.builder.ResumenCompra;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.facade.FachadaCliente;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClienteController {
    private final FachadaCliente fachada;
    @Getter private final Cliente cliente;

    public ClienteController(FachadaCliente fachada, Cliente cliente) {
        this.fachada = fachada;
        this.cliente = cliente;
    }

    public record SeleccionConfiteria(ItemConfiteria item, int cantidad, boolean conPuntos) { }

    public List<Pelicula> cartelera() { return fachada.peliculasEnCartelera(); }
    public List<Funcion> funciones(Pelicula p) { return fachada.funcionesDisponibles(p); }
    public List<ItemConfiteria> confiteria() { return fachada.confiteriaDisponible(); }
    public List<Promocion> promociones(LocalDate fecha) { return fachada.promocionesAplicables(fecha); }

    private static List<LineaConfiteria> lineas(List<SeleccionConfiteria> items) {
        return items.stream().map(i -> new LineaConfiteria(i.item(), i.cantidad(), i.conPuntos())).toList();
    }

    public ResumenCompra previsualizar(Funcion f, List<String> asientos, Set<String> conPuntos, List<SeleccionConfiteria> items) {
        return fachada.previsualizarCompra(cliente, f, asientos, conPuntos, lineas(items));
    }

    public Compra comprar(Funcion f, List<String> asientos, Set<String> conPuntos, List<SeleccionConfiteria> items) {
        return fachada.comprar(cliente, f, asientos, conPuntos, lineas(items));
    }

    public List<Compra> historial() { return fachada.historialCompras(cliente); }
    public double porcentajeReembolso(Compra c) { return fachada.porcentajeReembolso(c); }
    public Reembolso cancelar(Compra c) { return fachada.cancelarCompra(c, cliente); }
    public SolicitudRecarga solicitarRecarga(String monto) { return fachada.solicitarRecarga(cliente, Conversor.decimal(monto, "monto")); }
    public List<SolicitudRecarga> solicitudes() { return fachada.misSolicitudes(cliente); }
    public int puntosDisponibles() { return fachada.puntosDisponibles(cliente); }
    public String reglaPuntos() { return fachada.reglaPuntos(); }
    public LocalDate hoy() { return fachada.hoy(); }

    public Map<String, String> equivalenciasPuntos() { return fachada.equivalenciasPuntos(); }
    public int maxEntradas() { return fachada.maxEntradasPorCompra(); }
    public List<MovimientoTarjeta> movimientosTarjeta() { return fachada.movimientosTarjeta(cliente); }
    public List<LotePuntos> lotesPuntos() { return fachada.lotesPuntos(cliente); }
    public boolean loteVigente(LotePuntos l) { return fachada.loteVigente(l); }
    public List<MovimientoPuntos> movimientosPuntos() { return fachada.movimientosPuntos(cliente); }
}
