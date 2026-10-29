package co.edu.uniquindio.poo.patrones.facade;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.enums.ClasificacionEdad;
import co.edu.uniquindio.poo.enums.EstadoTarjeta;
import co.edu.uniquindio.poo.enums.FormatoReporte;
import co.edu.uniquindio.poo.enums.GeneroPelicula;
import co.edu.uniquindio.poo.enums.TipoPromocion;
import co.edu.uniquindio.poo.enums.TipoReporte;
import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.MovimientoTarjeta;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.model.Sala;
import co.edu.uniquindio.poo.model.SolicitudRecarga;
import co.edu.uniquindio.poo.patrones.composite.Combo;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.composite.Producto;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface FachadaAdministrador {
    List<Pelicula> listarPeliculas();
    Pelicula crearPelicula(String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion, String sinopsis);
    void actualizarPelicula(Pelicula p, String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion, String sinopsis, boolean enCartelera);
    void eliminarPelicula(Pelicula p);

    List<Sala> listarSalas();
    Sala crearSala(String nombre, TipoSala tipo, int filas, int columnas);
    void actualizarSala(Sala s, String nombre, TipoSala tipo, int filas, int columnas);
    void eliminarSala(Sala s);
    void cambiarHabilitacionAsiento(Sala s, Asiento a);

    List<Funcion> listarFunciones();
    Funcion crearFuncion(Pelicula p, Sala s, LocalDate fecha, LocalTime hora, double precio);
    int cancelarFuncion(Funcion f);
    void eliminarFuncion(Funcion f);

    List<ItemConfiteria> listarConfiteria();
    List<Producto> listarProductos();
    Producto crearProducto(String nombre, double precio, CategoriaProducto categoria, int puntos);
    void actualizarProducto(Producto p, String nombre, double precio, CategoriaProducto categoria, int puntos, boolean activo);
    Combo crearCombo(String nombre, double descuento, List<ItemConfiteria> componentes);
    void actualizarCombo(Combo c, String nombre, double descuento, List<ItemConfiteria> componentes, boolean activo);
    void eliminarItem(ItemConfiteria item);

    List<Promocion> listarPromociones();
    Promocion crearPromocion(String nombre, String descripcion, double porcentaje, TipoPromocion tipo, LocalDate inicio, LocalDate fin, DayOfWeek dia);
    void actualizarPromocion(Promocion p, String nombre, String descripcion, double porcentaje, TipoPromocion tipo, LocalDate inicio, LocalDate fin, DayOfWeek dia, boolean activa);
    void eliminarPromocion(Promocion p);

    List<Cliente> listarClientes();
    void recargarTarjeta(Cliente c, double monto);
    void cambiarEstadoTarjeta(Cliente c, EstadoTarjeta estado);
    List<SolicitudRecarga> solicitudesRecarga();
    void aprobarSolicitud(SolicitudRecarga s);
    void rechazarSolicitud(SolicitudRecarga s);

    List<Compra> listarCompras();

    Reporte generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta);
    void exportarReporte(Reporte reporte, FormatoReporte formato, File destino) throws IOException;
    Map<LocalDate, Double> ventasPorDia(LocalDate d, LocalDate h);
    Map<LocalDate, Double> recargasPorDia(LocalDate d, LocalDate h);
    Map<String, Integer> entradasPorPelicula(LocalDate d, LocalDate h);
    Map<String, Integer> productosMasVendidos(LocalDate d, LocalDate h);
    Map<String, Double> ventasPorCategoria(LocalDate d, LocalDate h);
    Map<String, Double> ocupacionPorSala(LocalDate d, LocalDate h);
    double totalVentas(LocalDate d, LocalDate h);
    int totalEntradas(LocalDate d, LocalDate h);
    int totalProductos(LocalDate d, LocalDate h);
    double totalReembolsos(LocalDate d, LocalDate h);
    LocalDate hoy();

    List<Funcion> funcionesRecientesPrimero();
    List<SolicitudRecarga> solicitudesPorAtender();
    List<MovimientoTarjeta> movimientosTarjeta(Cliente cliente);
    int puntosDisponibles(Cliente cliente);
    Map<String, Integer> topEntradasPorPelicula(LocalDate desde, LocalDate hasta, int n);
    Map<String, Integer> topProductos(LocalDate desde, LocalDate hasta, int n);
    Map<String, Double> porcentajeVentasPorCategoria(LocalDate desde, LocalDate hasta);

    void guardarPelicula(Pelicula p, String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion, String sinopsis, boolean enCartelera);
    void guardarSala(Sala s, String nombre, TipoSala tipo, int filas, int columnas);
    void guardarProducto(Producto p, String nombre, double precio, CategoriaProducto categoria, int puntos, boolean activo);
    void guardarCombo(Combo c, String nombre, double descuento, List<ItemConfiteria> componentes, boolean activo);
    void guardarPromocion(Promocion p, String nombre, String descripcion, double porcentaje, TipoPromocion tipo, LocalDate inicio, LocalDate fin, DayOfWeek dia, boolean activa);
}
