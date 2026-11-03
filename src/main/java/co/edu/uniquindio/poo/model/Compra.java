package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.patrones.factory.Entrada;
import co.edu.uniquindio.poo.patrones.state.EstadoAnulada;
import co.edu.uniquindio.poo.patrones.state.EstadoCompra;
import co.edu.uniquindio.poo.patrones.state.EstadoPagada;
import co.edu.uniquindio.poo.patrones.state.EstadoPendiente;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compra implements Identificable {
    @Getter private final String id;
    @Getter private final Cliente cliente;
    @Getter private final Funcion funcion;
    private final List<Entrada> entradas;
    private final List<LineaConfiteria> confiteria;
    @Getter private final LocalDateTime fecha;
    @Getter private final double subtotalEntradas;
    @Getter private final double subtotalConfiteria;
    @Getter private final double total;
    @Getter private final int puntosUsados;
    private final List<String> promocionesAplicadas;

    @Getter private EstadoCompra estado = new EstadoPendiente();
    @Getter @Setter private int puntosGanados;
    @Getter @Setter private LotePuntos loteGenerado;
    @Getter private List<ConsumoPuntos> consumosPuntos = new ArrayList<>();
    @Getter private Reembolso reembolso;

    public Compra(String id, Cliente cliente, Funcion funcion, List<Entrada> entradas, List<LineaConfiteria> confiteria,
                  double subtotalEntradas, double subtotalConfiteria, double total, int puntosUsados,
                  List<String> promocionesAplicadas, LocalDateTime fecha) {
        this.id = id;
        this.cliente = cliente;
        this.funcion = funcion;
        this.entradas = new ArrayList<>(entradas);
        this.confiteria = new ArrayList<>(confiteria);
        this.subtotalEntradas = subtotalEntradas;
        this.subtotalConfiteria = subtotalConfiteria;
        this.total = total;
        this.puntosUsados = puntosUsados;
        this.promocionesAplicadas = new ArrayList<>(promocionesAplicadas);
        this.fecha = fecha;
    }

    public void confirmarPago() { estado.pagar(this); }
    public void registrarReembolso(Reembolso r) { estado.reembolsar(this, r); }
    public void anular() { estado.anular(this); }

    public String getCodigosAsientos() {
        return String.join(", ", entradas.stream().map(e -> e.getAsiento().getCodigo()).toList());
    }

    public boolean puedeCancelarse() { return estado.permiteCancelacion(); }
    public boolean estaPagada() { return estado instanceof EstadoPagada; }
    public boolean estaAnulada() { return estado instanceof EstadoAnulada; }

    public void cambiarEstado(EstadoCompra nuevo) { this.estado = nuevo; }
    public void asignarReembolso(Reembolso r) { this.reembolso = r; }

    public double getDescuento() { return Math.max(0, subtotalEntradas + subtotalConfiteria - total); }
    public int getCantidadEntradas() { return entradas.size(); }

    public List<Entrada> getEntradas() { return Collections.unmodifiableList(entradas); }
    public List<LineaConfiteria> getConfiteria() { return Collections.unmodifiableList(confiteria); }
    public List<String> getPromocionesAplicadas() { return Collections.unmodifiableList(promocionesAplicadas); }
    public String getNombreEstado() { return estado.getNombre(); }
    public void setConsumosPuntos(List<ConsumoPuntos> consumosPuntos) { this.consumosPuntos = new ArrayList<>(consumosPuntos); }
}
