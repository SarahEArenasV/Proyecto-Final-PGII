package co.edu.uniquindio.poo.patrones.facade;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FachadaCliente {
    List<Pelicula> peliculasEnCartelera();
    List<Funcion> funcionesDisponibles(Pelicula pelicula);
    List<ItemConfiteria> confiteriaDisponible();
    List<Promocion> promocionesAplicables(LocalDate fecha);
    ResumenCompra previsualizarCompra(Cliente cliente, Funcion funcion, List<String> asientos,
                                      Set<String> conPuntos, List<LineaConfiteria> confiteria);
    Compra comprar(Cliente cliente, Funcion funcion, List<String> asientos,
                   Set<String> conPuntos, List<LineaConfiteria> confiteria);
    Compra comprarPaqueteConCombo(Cliente cliente, Funcion funcion, List<String> asientos, ItemConfiteria combo);
    List<Compra> historialCompras(Cliente cliente);
    double porcentajeReembolso(Compra compra);
    Reembolso cancelarCompra(Compra compra, Cliente cliente);
    SolicitudRecarga solicitarRecarga(Cliente cliente, double monto);
    List<SolicitudRecarga> misSolicitudes(Cliente cliente);
    int puntosDisponibles(Cliente cliente);
    String reglaPuntos();
    LocalDate hoy();
    int maxEntradasPorCompra();
    List<MovimientoTarjeta> movimientosTarjeta(Cliente cliente);
    List<LotePuntos> lotesPuntos(Cliente cliente);
    boolean loteVigente(LotePuntos lote);
    List<MovimientoPuntos> movimientosPuntos(Cliente cliente);
    Map<String, String> equivalenciasPuntos();
}
