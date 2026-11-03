package co.edu.uniquindio.poo.controller;

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
import co.edu.uniquindio.poo.patrones.facade.FachadaAdministrador;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AdminController {
    private final FachadaAdministrador f;

    public AdminController(FachadaAdministrador fachada) { this.f = fachada; }

    public List<Pelicula> peliculas() { return f.listarPeliculas(); }
    public void guardarPelicula(Pelicula p, String titulo, GeneroPelicula g, String duracion, ClasificacionEdad c, String sinopsis, boolean cartelera) {
        f.guardarPelicula(p, titulo, g, Conversor.entero(duracion, "duración"), c, sinopsis, cartelera);
    }
    public void eliminarPelicula(Pelicula p) { f.eliminarPelicula(p); }

    public List<Sala> salas() { return f.listarSalas(); }
    public void guardarSala(Sala s, String nombre, TipoSala tipo, String filas, String columnas) {
        f.guardarSala(s, nombre, tipo, Conversor.entero(filas, "filas"), Conversor.entero(columnas, "columnas"));
    }
    public void eliminarSala(Sala s) { f.eliminarSala(s); }
    public void alternarAsiento(Sala s, Asiento a) { f.cambiarHabilitacionAsiento(s, a); }

    public List<Funcion> funciones() { return f.listarFunciones(); }
    public void crearFuncion(Pelicula p, Sala s, LocalDate fecha, String hora, String precio) {
        f.crearFuncion(p, s, fecha, Conversor.hora(hora), Conversor.decimal(precio, "precio"));
    }
    public int cancelarFuncion(Funcion fu) { return f.cancelarFuncion(fu); }
    public void eliminarFuncion(Funcion fu) { f.eliminarFuncion(fu); }

    public List<ItemConfiteria> confiteria() { return f.listarConfiteria(); }
    public void guardarProducto(Producto p, String nombre, String precio, CategoriaProducto cat, String puntos, boolean activo) {
        f.guardarProducto(p, nombre, Conversor.decimal(precio, "precio"), cat, Conversor.entero(puntos, "puntos"), activo);
    }
    public void guardarCombo(Combo c, String nombre, String descuento, List<ItemConfiteria> componentes, boolean activo) {
        f.guardarCombo(c, nombre, Conversor.decimal(descuento, "descuento"), componentes, activo);
    }
    public void eliminarItem(ItemConfiteria i) { f.eliminarItem(i); }

    public List<Promocion> promociones() { return f.listarPromociones(); }
    public void guardarPromocion(Promocion p, String nombre, String desc, String porcentaje, TipoPromocion tipo,
                                 LocalDate inicio, LocalDate fin, DayOfWeek dia, boolean activa) {
        f.guardarPromocion(p, nombre, desc, Conversor.decimal(porcentaje, "porcentaje"), tipo, inicio, fin, dia, activa);
    }
    public void eliminarPromocion(Promocion p) { f.eliminarPromocion(p); }

    public List<Cliente> clientes() { return f.listarClientes(); }
    public void recargar(Cliente c, String monto) { f.recargarTarjeta(c, Conversor.decimal(monto, "monto")); }
    public void cambiarEstadoTarjeta(Cliente c, EstadoTarjeta e) { f.cambiarEstadoTarjeta(c, e); }
    public List<SolicitudRecarga> solicitudes() { return f.solicitudesRecarga(); }
    public void aprobar(SolicitudRecarga s) { f.aprobarSolicitud(s); }
    public void rechazar(SolicitudRecarga s) { f.rechazarSolicitud(s); }
    public int puntos(Cliente c) { return f.puntosDisponibles(c); }
    public List<SolicitudRecarga> solicitudesPorAtender() { return f.solicitudesPorAtender(); }
    public List<MovimientoTarjeta> movimientosTarjeta(Cliente c) { return f.movimientosTarjeta(c); }
    public List<Funcion> funcionesRecientesPrimero() { return f.funcionesRecientesPrimero(); }
    public Map<String, Integer> topEntradasPorPelicula(LocalDate d, LocalDate h, int n) { return f.topEntradasPorPelicula(d, h, n); }
    public Map<String, Integer> topProductos(LocalDate d, LocalDate h, int n) { return f.topProductos(d, h, n); }
    public Map<String, Double> porcentajeVentasPorCategoria(LocalDate d, LocalDate h) { return f.porcentajeVentasPorCategoria(d, h); }

    public List<Compra> compras() { return f.listarCompras(); }
    public Reporte generarReporte(TipoReporte t, LocalDate d, LocalDate h) {
        return f.generarReporte(t, d, h);
    }
    public void exportar(Reporte r, FormatoReporte formato, File destino) throws IOException { f.exportarReporte(r, formato, destino); }
    public Map<LocalDate, Double> ventasPorDia(LocalDate d, LocalDate h) { return f.ventasPorDia(d, h); }
    public Map<LocalDate, Double> recargasPorDia(LocalDate d, LocalDate h) { return f.recargasPorDia(d, h); }
    public Map<String, Integer> entradasPorPelicula(LocalDate d, LocalDate h) { return f.entradasPorPelicula(d, h); }
    public Map<String, Integer> productosMasVendidos(LocalDate d, LocalDate h) { return f.productosMasVendidos(d, h); }
    public Map<String, Double> ventasPorCategoria(LocalDate d, LocalDate h) { return f.ventasPorCategoria(d, h); }
    public Map<String, Double> ocupacionPorSala(LocalDate d, LocalDate h) { return f.ocupacionPorSala(d, h); }
    public double totalVentas(LocalDate d, LocalDate h) { return f.totalVentas(d, h); }
    public int totalEntradas(LocalDate d, LocalDate h) { return f.totalEntradas(d, h); }
    public int totalProductos(LocalDate d, LocalDate h) { return f.totalProductos(d, h); }
    public double totalReembolsos(LocalDate d, LocalDate h) { return f.totalReembolsos(d, h); }
    public LocalDate hoy() { return f.hoy(); }
}
