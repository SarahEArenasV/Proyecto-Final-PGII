package co.edu.uniquindio.poo.patrones.builder;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.decorator.PrecioBaseCompra;
import co.edu.uniquindio.poo.patrones.decorator.PrecioCompra;
import co.edu.uniquindio.poo.patrones.decorator.PromocionDecorator;
import co.edu.uniquindio.poo.patrones.decorator.TopeDescuentoDecorator;
import co.edu.uniquindio.poo.patrones.factory.Entrada;
import co.edu.uniquindio.poo.patrones.factory.FabricaEntrada;
import co.edu.uniquindio.poo.patrones.factory.ProveedorFabricaEntrada;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class ConstructorCompraBase implements ConstructorCompra {
    public static final int MAX_ENTRADAS = 10;

    private final ProveedorFabricaEntrada proveedorFabricas;
    protected Cliente cliente;
    protected Funcion funcion;
    protected LocalDateTime fecha = LocalDateTime.now();
    protected final List<String> asientos = new ArrayList<>();
    protected final Set<String> asientosConPuntos = new HashSet<>();
    protected final List<LineaConfiteria> confiteria = new ArrayList<>();
    protected final List<Promocion> promociones = new ArrayList<>();

    protected ConstructorCompraBase(ProveedorFabricaEntrada proveedorFabricas) {
        this.proveedorFabricas = Objects.requireNonNull(proveedorFabricas);
    }

    @Override public ConstructorCompraBase reiniciar() {
        asientos.clear();
        asientosConPuntos.clear();
        confiteria.clear();
        return this;
    }

    @Override public ConstructorCompraBase cliente(Cliente cliente) { this.cliente = cliente; return this; }
    @Override public ConstructorCompraBase funcion(Funcion funcion) { this.funcion = funcion; return this; }
    @Override public ConstructorCompraBase fecha(LocalDateTime fecha) { this.fecha = fecha; return this; }

    @Override public ConstructorCompraBase conPromociones(List<Promocion> lista) {
        promociones.clear();
        if (lista != null) promociones.addAll(lista);
        return this;
    }

    @Override public ConstructorCompraBase agregarEntrada(String codigoAsiento, boolean conPuntos) {
        if (codigoAsiento == null || codigoAsiento.isBlank()) throw new DatosInvalidosException("Seleccione un asiento válido");
        asientos.add(codigoAsiento);
        if (conPuntos) asientosConPuntos.add(codigoAsiento);
        return this;
    }

    @Override public ConstructorCompraBase agregarConfiteria(ItemConfiteria item, int cantidad, boolean conPuntos) {
        if (item == null) throw new DatosInvalidosException("Seleccione un producto o combo");
        if (cantidad <= 0) throw new DatosInvalidosException("La cantidad debe ser mayor que cero");
        if (!item.isActivo()) throw new DatosInvalidosException(item.getNombre() + " no está disponible");
        confiteria.add(new LineaConfiteria(item, cantidad, conPuntos));
        return this;
    }

    protected void validar() {
        if (cliente == null) throw new DatosInvalidosException("La compra debe tener un cliente");
        if (funcion == null) throw new DatosInvalidosException("Debe seleccionar una función");
        if (asientos.isEmpty()) throw new DatosInvalidosException("Debe seleccionar al menos un asiento");
        if (asientos.size() > MAX_ENTRADAS) throw new DatosInvalidosException("Máximo " + MAX_ENTRADAS + " entradas por compra");
        if (new HashSet<>(asientos).size() != asientos.size()) throw new DatosInvalidosException("Hay asientos repetidos");
    }

    protected record Calculo(List<Entrada> entradas, double subtotalEntradas, double subtotalConfiteria,
                             int puntos, PrecioCompra precio) {
        long total() { return Math.round(precio.getTotal()); }
    }

    protected Calculo calcular(boolean definitivo) {
        FabricaEntrada fabrica = proveedorFabricas.obtener(funcion.getSala().getTipo());
        List<Entrada> entradas = new ArrayList<>();
        double subtotalEntradas = 0;
        int puntos = 0;
        for (String codigo : asientos) {
            Entrada entrada = definitivo ? fabrica.crear(funcion, codigo) : fabrica.crear("PREVIA", funcion, codigo);
            if (asientosConPuntos.contains(codigo)) {
                entrada.setRedimidaConPuntos(true);
                puntos += entrada.getPuntosRedencion();
            } else {
                subtotalEntradas += entrada.calcularPrecio();
            }
            entradas.add(entrada);
        }
        double subtotalConfiteria = 0;
        for (LineaConfiteria linea : confiteria) {
            if (linea.isRedimidaConPuntos()) puntos += linea.getPuntosRequeridos();
            else subtotalConfiteria += linea.getSubtotal();
        }

        PrecioCompra precio = new PrecioBaseCompra(subtotalEntradas, subtotalConfiteria);
        for (Promocion p : promociones) {
            if (p.aplicaPara(funcion.getFecha())) precio = PromocionDecorator.aplicar(precio, p);
        }
        precio = new TopeDescuentoDecorator(precio, TopeDescuentoDecorator.MAXIMO_PERMITIDO);
        return new Calculo(entradas, subtotalEntradas, subtotalConfiteria, puntos, precio);
    }
}
