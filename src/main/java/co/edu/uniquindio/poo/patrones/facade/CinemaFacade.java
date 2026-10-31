package co.edu.uniquindio.poo.patrones.facade;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.enums.ClasificacionEdad;
import co.edu.uniquindio.poo.enums.EstadoTarjeta;
import co.edu.uniquindio.poo.enums.FormatoReporte;
import co.edu.uniquindio.poo.enums.GeneroPelicula;
import co.edu.uniquindio.poo.enums.TipoPromocion;
import co.edu.uniquindio.poo.enums.TipoReporte;
import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Asiento;
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
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.model.Sala;
import co.edu.uniquindio.poo.model.SolicitudRecarga;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.patrones.builder.ConstructorCompraBase;
import co.edu.uniquindio.poo.patrones.builder.ResumenCompra;
import co.edu.uniquindio.poo.patrones.composite.Combo;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.composite.Producto;
import co.edu.uniquindio.poo.servicios.AutenticacionService;
import co.edu.uniquindio.poo.servicios.CompraService;
import co.edu.uniquindio.poo.servicios.ConfiteriaService;
import co.edu.uniquindio.poo.servicios.FuncionService;
import co.edu.uniquindio.poo.servicios.PeliculaService;
import co.edu.uniquindio.poo.servicios.PromocionService;
import co.edu.uniquindio.poo.servicios.PuntosService;
import co.edu.uniquindio.poo.servicios.Repositorio;
import co.edu.uniquindio.poo.servicios.RepositorioMemoria;
import co.edu.uniquindio.poo.servicios.SalaService;
import co.edu.uniquindio.poo.servicios.TarjetaService;
import co.edu.uniquindio.poo.patrones.factory.ProveedorFabricaEntrada;
import co.edu.uniquindio.poo.patrones.proxy.ServicioRecargaProxy;
import co.edu.uniquindio.poo.patrones.proxy.ServicioRecargaReal;
import co.edu.uniquindio.poo.patrones.strategy.ReglaPuntosPorValor;
import co.edu.uniquindio.poo.patrones.templatemethod.FuenteDatosReporte;
import co.edu.uniquindio.poo.util.Reloj;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CinemaFacade implements FachadaAutenticacion, FachadaCliente, FachadaAdministrador {
    @Getter private final Reloj reloj = new Reloj();
    private final AutenticacionService autenticacion;
    private final PeliculaService peliculas;
    private final SalaService salas;
    private final FuncionService funciones;
    private final ConfiteriaService confiteria;
    private final PromocionService promociones;
    private final TarjetaService tarjetas;
    private final PuntosService puntos;
    private final CompraService compras;
    private final FachadaAnalitica analitica;

    private static final class Holder { private static final CinemaFacade INSTANCIA = new CinemaFacade(); }

    public static CinemaFacade getInstancia() { return Holder.INSTANCIA; }

    private CinemaFacade() {
        Repositorio<Usuario> repoUsuarios = new RepositorioMemoria<>(Usuario.class);
        Repositorio<Pelicula> repoPeliculas = new RepositorioMemoria<>(Pelicula.class);
        Repositorio<Sala> repoSalas = new RepositorioMemoria<>(Sala.class);
        Repositorio<Funcion> repoFunciones = new RepositorioMemoria<>(Funcion.class);
        Repositorio<ItemConfiteria> repoItems = new RepositorioMemoria<>(ItemConfiteria.class);
        Repositorio<Promocion> repoPromociones = new RepositorioMemoria<>(Promocion.class);
        Repositorio<Compra> repoCompras = new RepositorioMemoria<>(Compra.class);
        Repositorio<SolicitudRecarga> repoSolicitudes = new RepositorioMemoria<>(SolicitudRecarga.class);

        autenticacion = new AutenticacionService(repoUsuarios);
        peliculas = new PeliculaService(repoPeliculas, repoFunciones);
        salas = new SalaService(repoSalas, repoFunciones, reloj);
        funciones = new FuncionService(repoFunciones, reloj);
        confiteria = new ConfiteriaService(repoItems);
        promociones = new PromocionService(repoPromociones);
        tarjetas = new TarjetaService(repoSolicitudes,
                new ServicioRecargaProxy(new ServicioRecargaReal(reloj)));
        puntos = new PuntosService(new ReglaPuntosPorValor(1000), reloj);
        compras = new CompraService(repoCompras, new ProveedorFabricaEntrada(), promociones, puntos, reloj);

        FuenteDatosReporte fuente = new FuenteDatosReporte() {
            @Override public List<Compra> compras() { return repoCompras.listar(); }
            @Override public List<Cliente> clientes() { return autenticacion.clientes(); }
            @Override public List<Funcion> funciones() { return repoFunciones.listar(); }
        };
        analitica = new FachadaAnalitica(fuente);
    }

    public void registrarUsuario(Usuario u) { autenticacion.registrarUsuario(u); }

    @Override public Usuario iniciarSesion(String correo, String clave) { return autenticacion.iniciarSesion(correo, clave); }
    @Override public Cliente registrarCliente(String n, String d, String c, String t, String k) { return autenticacion.registrarCliente(n, d, c, t, k); }
    @Override public Cliente registrarCliente(String n, String d, String c, String t, String k, String conf) { return autenticacion.registrarCliente(n, d, c, t, k, conf); }
    @Override public void cerrarSesion() { autenticacion.cerrarSesion(); }

    @Override public List<Pelicula> peliculasEnCartelera() {
        return peliculas.enCartelera().stream().filter(p -> !funciones.disponibles(p).isEmpty()).toList();
    }
    @Override public List<Funcion> funcionesDisponibles(Pelicula p) { return funciones.disponibles(p); }
    @Override public List<ItemConfiteria> confiteriaDisponible() { return confiteria.disponibles(); }
    @Override public List<Promocion> promocionesAplicables(LocalDate fecha) { return promociones.aplicables(fecha); }
    @Override public ResumenCompra previsualizarCompra(Cliente c, Funcion f, List<String> a, Set<String> p, List<LineaConfiteria> l) { return compras.previsualizar(c, f, a, p, l); }
    @Override public Compra comprar(Cliente c, Funcion f, List<String> a, Set<String> p, List<LineaConfiteria> l) { return compras.comprar(c, f, a, p, l); }
    @Override public Compra comprarPaqueteConCombo(Cliente c, Funcion f, List<String> a, ItemConfiteria combo) { return compras.comprarPaqueteConCombo(c, f, a, combo); }
    @Override public List<Compra> historialCompras(Cliente c) { return compras.historial(c); }
    @Override public double porcentajeReembolso(Compra c) { return compras.porcentajeReembolsoActual(c); }
    @Override public Reembolso cancelarCompra(Compra compra, Cliente c) { return compras.cancelarPorCliente(compra, c); }
    @Override public SolicitudRecarga solicitarRecarga(Cliente c, double monto) { return tarjetas.solicitarRecarga(c, monto); }
    @Override public List<SolicitudRecarga> misSolicitudes(Cliente c) { return tarjetas.solicitudesDe(c); }
    @Override public int puntosDisponibles(Cliente c) { return puntos.disponibles(c); }
    @Override public String reglaPuntos() { return puntos.descripcionRegla(); }
    @Override public LocalDate hoy() { return reloj.hoy(); }

    @Override public List<Pelicula> listarPeliculas() { return peliculas.listar(); }
    @Override public Pelicula crearPelicula(String t, GeneroPelicula g, int d, ClasificacionEdad c, String s) { return peliculas.crear(t, g, d, c, s); }
    @Override public void actualizarPelicula(Pelicula p, String t, GeneroPelicula g, int d, ClasificacionEdad c, String s, boolean e) { peliculas.actualizar(p, t, g, d, c, s, e); }
    @Override public void eliminarPelicula(Pelicula p) { peliculas.eliminar(requerir(p)); }
    @Override public List<Sala> listarSalas() { return salas.listar(); }
    @Override public Sala crearSala(String n, TipoSala t, int f, int c) { return salas.crear(n, t, f, c); }
    @Override public void actualizarSala(Sala s, String n, TipoSala t, int f, int c) { salas.actualizar(s, n, t, f, c); }
    @Override public void eliminarSala(Sala s) { salas.eliminar(requerir(s)); }
    @Override public void cambiarHabilitacionAsiento(Sala s, Asiento a) { salas.cambiarHabilitacionAsiento(requerir(s), requerir(a)); }
    @Override public List<Funcion> listarFunciones() { return funciones.listar(); }
    @Override public Funcion crearFuncion(Pelicula p, Sala s, LocalDate f, LocalTime h, double precio) { return funciones.crear(p, s, f, h, precio); }
    @Override public int cancelarFuncion(Funcion f) {
        funciones.marcarCancelada(requerir(f));
        return compras.reembolsarPorCancelacionFuncion(f);
    }
    @Override public void eliminarFuncion(Funcion f) { funciones.eliminar(requerir(f)); }
    @Override public List<ItemConfiteria> listarConfiteria() { return confiteria.listar(); }
    @Override public List<Producto> listarProductos() { return confiteria.productos(); }
    @Override public Producto crearProducto(String n, double p, CategoriaProducto c, int pts) { return confiteria.crearProducto(n, p, c, pts); }
    @Override public void actualizarProducto(Producto p, String n, double pr, CategoriaProducto c, int pts, boolean a) { confiteria.actualizarProducto(p, n, pr, c, pts, a); }
    @Override public Combo crearCombo(String n, double d, List<ItemConfiteria> comp) { return confiteria.crearCombo(n, d, comp); }
    @Override public void actualizarCombo(Combo c, String n, double d, List<ItemConfiteria> comp, boolean a) { confiteria.actualizarCombo(c, n, d, comp, a); }
    @Override public void eliminarItem(ItemConfiteria i) { confiteria.eliminar(requerir(i)); }
    @Override public List<Promocion> listarPromociones() { return promociones.listar(); }
    @Override public Promocion crearPromocion(String n, String d, double p, TipoPromocion t, LocalDate i, LocalDate f, DayOfWeek dia) { return promociones.crear(n, d, p, t, i, f, dia); }
    @Override public void actualizarPromocion(Promocion pr, String n, String d, double p, TipoPromocion t, LocalDate i, LocalDate f, DayOfWeek dia, boolean a) { promociones.actualizar(pr, n, d, p, t, i, f, dia, a); }
    @Override public void eliminarPromocion(Promocion p) { promociones.eliminar(requerir(p)); }
    @Override public List<Cliente> listarClientes() { return autenticacion.clientes(); }
    @Override public void recargarTarjeta(Cliente c, double monto) { tarjetas.recargar(requerir(c), monto); }
    @Override public void cambiarEstadoTarjeta(Cliente c, EstadoTarjeta e) { tarjetas.cambiarEstadoTarjeta(requerir(c), e); }
    @Override public List<SolicitudRecarga> solicitudesRecarga() { return tarjetas.solicitudes(); }
    @Override public void aprobarSolicitud(SolicitudRecarga s) { tarjetas.aprobarSolicitud(requerir(s)); }
    @Override public void rechazarSolicitud(SolicitudRecarga s) { tarjetas.rechazarSolicitud(requerir(s)); }
    @Override public List<Compra> listarCompras() { return compras.listar(); }
    @Override public Reporte generarReporte(TipoReporte t, LocalDate d, LocalDate h) { return analitica.generarReporte(t, d, h); }
    @Override public void exportarReporte(Reporte r, FormatoReporte f, File destino) throws IOException { analitica.exportarReporte(requerir(r), f, destino); }
    @Override public Map<LocalDate, Double> ventasPorDia(LocalDate d, LocalDate h) { return analitica.ventasPorDia(d, h); }
    @Override public Map<LocalDate, Double> recargasPorDia(LocalDate d, LocalDate h) { return analitica.recargasPorDia(d, h); }
    @Override public Map<String, Integer> entradasPorPelicula(LocalDate d, LocalDate h) { return analitica.entradasPorPelicula(d, h); }
    @Override public Map<String, Integer> productosMasVendidos(LocalDate d, LocalDate h) { return analitica.productosMasVendidos(d, h); }
    @Override public Map<String, Double> ventasPorCategoria(LocalDate d, LocalDate h) { return analitica.ventasPorCategoria(d, h); }
    @Override public Map<String, Double> ocupacionPorSala(LocalDate d, LocalDate h) { return analitica.ocupacionPorSala(d, h); }
    @Override public double totalVentas(LocalDate d, LocalDate h) { return analitica.totalVentas(d, h); }
    @Override public int totalEntradas(LocalDate d, LocalDate h) { return analitica.totalEntradas(d, h); }
    @Override public int totalProductos(LocalDate d, LocalDate h) { return analitica.totalProductos(d, h); }
    @Override public double totalReembolsos(LocalDate d, LocalDate h) { return analitica.totalReembolsos(d, h); }

    @Override public int maxEntradasPorCompra() { return ConstructorCompraBase.MAX_ENTRADAS; }
    @Override public List<MovimientoTarjeta> movimientosTarjeta(Cliente c) { return tarjetas.movimientos(c); }
    @Override public List<LotePuntos> lotesPuntos(Cliente c) { return puntos.lotes(c); }
    @Override public boolean loteVigente(LotePuntos l) { return puntos.loteVigente(l); }
    @Override public List<MovimientoPuntos> movimientosPuntos(Cliente c) { return puntos.movimientos(c); }
    @Override public Map<String, String> equivalenciasPuntos() { return puntos.equivalencias(); }
    @Override public List<Funcion> funcionesRecientesPrimero() { return funciones.listarRecientesPrimero(); }
    @Override public List<SolicitudRecarga> solicitudesPorAtender() { return tarjetas.solicitudesPorAtender(); }
    @Override public Map<String, Integer> topEntradasPorPelicula(LocalDate d, LocalDate h, int n) { return analitica.topEntradasPorPelicula(d, h, n); }
    @Override public Map<String, Integer> topProductos(LocalDate d, LocalDate h, int n) { return analitica.topProductos(d, h, n); }
    @Override public Map<String, Double> porcentajeVentasPorCategoria(LocalDate d, LocalDate h) { return analitica.porcentajeVentasPorCategoria(d, h); }

    @Override public void guardarPelicula(Pelicula p, String t, GeneroPelicula g, int d, ClasificacionEdad c, String s, boolean e) { peliculas.guardar(p, t, g, d, c, s, e); }
    @Override public void guardarSala(Sala s, String n, TipoSala t, int f, int c) { salas.guardar(s, n, t, f, c); }
    @Override public void guardarProducto(Producto p, String n, double pr, CategoriaProducto c, int pts, boolean a) { confiteria.guardarProducto(p, n, pr, c, pts, a); }
    @Override public void guardarCombo(Combo c, String n, double d, List<ItemConfiteria> comp, boolean a) { confiteria.guardarCombo(c, n, d, comp, a); }
    @Override public void guardarPromocion(Promocion pr, String n, String d, double p, TipoPromocion t, LocalDate i, LocalDate f, DayOfWeek dia, boolean a) { promociones.guardar(pr, n, d, p, t, i, f, dia, a); }

    private static <T> T requerir(T valor) {
        if (valor == null) throw new DatosInvalidosException("Seleccione un registro de la tabla");
        return valor;
    }
}
