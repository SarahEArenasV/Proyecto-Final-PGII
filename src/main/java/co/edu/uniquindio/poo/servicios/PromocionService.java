package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.TipoPromocion;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class PromocionService {
    private final Repositorio<Promocion> promociones;

    public PromocionService(Repositorio<Promocion> promociones) { this.promociones = promociones; }

    public Promocion crear(String nombre, String descripcion, double porcentaje, TipoPromocion tipo,
                           LocalDate inicio, LocalDate fin, DayOfWeek dia) {
        validar(nombre, porcentaje, tipo, inicio, fin);
        Promocion p = new Promocion(BaseDatos.getInstancia().siguienteId("PRM"), nombre.trim(), descripcion, porcentaje, tipo, inicio, fin, dia);
        promociones.guardar(p);
        return p;
    }

    public void actualizar(Promocion p, String nombre, String descripcion, double porcentaje, TipoPromocion tipo,
                           LocalDate inicio, LocalDate fin, DayOfWeek dia, boolean activa) {
        validar(nombre, porcentaje, tipo, inicio, fin);
        p.setNombre(nombre.trim());
        p.setDescripcion(descripcion);
        p.setPorcentaje(porcentaje);
        p.setTipo(tipo);
        p.setFechaInicio(inicio);
        p.setFechaFin(fin);
        p.setDiaAplicable(dia);
        p.setActiva(activa);
    }

    public void eliminar(Promocion p) { promociones.eliminar(p.getId()); }
    public List<Promocion> listar() { return promociones.listar(); }
    public List<Promocion> activas() { return promociones.listar().stream().filter(Promocion::isActiva).toList(); }
    public List<Promocion> aplicables(LocalDate fecha) { return activas().stream().filter(p -> p.aplicaPara(fecha)).toList(); }

    private void validar(String nombre, double porcentaje, TipoPromocion tipo, LocalDate inicio, LocalDate fin) {
        Validador.requerido(nombre, "nombre");
        Validador.porcentaje(porcentaje);
        if (porcentaje > 70) throw new DatosInvalidosException("Una promoción no puede superar el 70 %");
        if (tipo == null || inicio == null || fin == null) throw new DatosInvalidosException("Complete tipo y vigencia");
        if (fin.isBefore(inicio)) throw new DatosInvalidosException("La fecha final debe ser posterior a la inicial");
    }

    public void guardar(Promocion p, String nombre, String descripcion, double porcentaje, TipoPromocion tipo,
                        LocalDate inicio, LocalDate fin, DayOfWeek dia, boolean activa) {
        if (p == null) crear(nombre, descripcion, porcentaje, tipo, inicio, fin, dia);
        else actualizar(p, nombre, descripcion, porcentaje, tipo, inicio, fin, dia, activa);
    }
}
