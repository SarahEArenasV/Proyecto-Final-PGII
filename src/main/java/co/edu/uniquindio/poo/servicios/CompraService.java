package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.exceptions.CancelacionNoPermitidaException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.ConsumoPuntos;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.model.Reembolso;
import co.edu.uniquindio.poo.patrones.builder.CompraBuilder;
import co.edu.uniquindio.poo.patrones.builder.ConstructorCompra;
import co.edu.uniquindio.poo.patrones.builder.DirectorCompra;
import co.edu.uniquindio.poo.patrones.builder.ResumenCompra;
import co.edu.uniquindio.poo.patrones.builder.ResumenCompraBuilder;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.factory.ProveedorFabricaEntrada;
import co.edu.uniquindio.poo.patrones.strategy.ContextoReembolso;
import co.edu.uniquindio.poo.patrones.strategy.PoliticaReembolso;
import co.edu.uniquindio.poo.patrones.strategy.PoliticaReembolsoCine;
import co.edu.uniquindio.poo.patrones.strategy.PoliticaReembolsoCliente;
import co.edu.uniquindio.poo.util.Reloj;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class CompraService {
    private final Repositorio<Compra> compras;
    private final ProveedorFabricaEntrada fabricas;
    private final PromocionService promociones;
    private final PuntosService puntosService;
    private final Reloj reloj;
    private final PoliticaReembolso politicaCliente = new PoliticaReembolsoCliente();
    private final PoliticaReembolso politicaCine = new PoliticaReembolsoCine();
    private final DirectorCompra director = new DirectorCompra();
    private final ContextoReembolso contextoReembolso = new ContextoReembolso(politicaCliente);

    public CompraService(Repositorio<Compra> compras, ProveedorFabricaEntrada fabricas, PromocionService promociones,
                         PuntosService puntosService, Reloj reloj) {
        this.compras = compras;
        this.fabricas = fabricas;
        this.promociones = promociones;
        this.puntosService = puntosService;
        this.reloj = reloj;
    }

    private CompraBuilder nuevaCompra(Cliente cliente, Funcion funcion) {
        return preparar(new CompraBuilder(fabricas), cliente, funcion);
    }

    private ResumenCompraBuilder nuevaVistaPrevia(Cliente cliente, Funcion funcion) {
        return preparar(new ResumenCompraBuilder(fabricas), cliente, funcion);
    }

    private <B extends ConstructorCompra> B preparar(B builder, Cliente cliente, Funcion funcion) {
        builder.cliente(cliente).funcion(funcion).fecha(reloj.ahora()).conPromociones(promociones.activas());
        return builder;
    }

    public ResumenCompra previsualizar(Cliente cliente, Funcion funcion, List<String> asientos,
                                       Set<String> conPuntos, List<LineaConfiteria> confiteria) {
        ResumenCompraBuilder builder = nuevaVistaPrevia(cliente, funcion);
        director.construirPersonalizada(builder, asientos, conPuntos, confiteria);
        return builder.getResultado();
    }

    public Compra comprar(Cliente cliente, Funcion funcion, List<String> asientos,
                          Set<String> conPuntos, List<LineaConfiteria> confiteria) {
        CompraBuilder builder = nuevaCompra(cliente, funcion);
        director.construirPersonalizada(builder, asientos, conPuntos, confiteria);
        return confirmar(builder.getResultado());
    }

    public Compra comprarPaqueteConCombo(Cliente cliente, Funcion funcion, List<String> asientos, ItemConfiteria combo) {
        CompraBuilder builder = nuevaCompra(cliente, funcion);
        director.construirPaqueteConCombo(builder, asientos, combo);
        return confirmar(builder.getResultado());
    }

    private Compra confirmar(Compra compra) {
        compra.confirmarPago();
        compras.guardar(compra);
        puntosService.acumularPorCompra(compra);
        return compra;
    }

    public Reembolso cancelarPorCliente(Compra compra, Cliente solicitante) {
        if (!compra.getCliente().equals(solicitante)) throw new CancelacionNoPermitidaException("La compra no pertenece al cliente");
        return procesarReembolso(compra, politicaCliente);
    }

    public int reembolsarPorCancelacionFuncion(Funcion funcion) {
        List<Compra> afectadas = compras.listar().stream()
                .filter(c -> c.getFuncion().equals(funcion) && c.puedeCancelarse()).toList();
        for (Compra c : afectadas) {
            procesarReembolso(c, politicaCine);
        }
        return afectadas.size();
    }

    private Reembolso procesarReembolso(Compra compra, PoliticaReembolso politica) {
        LocalDateTime ahora = reloj.ahora();
        contextoReembolso.setPolitica(politica);
        double porcentaje = contextoReembolso.calcularPorcentaje(compra, ahora);
        int puntosDevueltos = compra.getConsumosPuntos().stream().mapToInt(ConsumoPuntos::cantidad).sum();
        Reembolso reembolso = new Reembolso(compra.getTotal(), porcentaje, puntosDevueltos,
                contextoReembolso.getDescripcion(), ahora);
        compra.registrarReembolso(reembolso);
        return reembolso;
    }

    public double porcentajeReembolsoActual(Compra compra) { return politicaCliente.calcularPorcentaje(compra, reloj.ahora()); }

    public List<Compra> historial(Cliente c) {
        return listar().stream().filter(x -> x.getCliente().equals(c)).toList();
    }

    public List<Compra> listar() {
        return compras.listar().stream().sorted(Comparator.comparing(Compra::getFecha).reversed()).toList();
    }
}
