package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.EstadoFuncion;
import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Sala;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;
import co.edu.uniquindio.poo.util.Reloj;

import java.util.List;

public class SalaService {
    private final Repositorio<Sala> salas;
    private final Repositorio<Funcion> funciones;
    private final Reloj reloj;

    public SalaService(Repositorio<Sala> salas, Repositorio<Funcion> funciones, Reloj reloj) {
        this.salas = salas;
        this.funciones = funciones;
        this.reloj = reloj;
    }

    public Sala crear(String nombre, TipoSala tipo, int filas, int columnas) {
        Validador.requerido(nombre, "nombre");
        if (tipo == null) throw new DatosInvalidosException("Seleccione el tipo de sala");
        if (salas.listar().stream().anyMatch(s -> s.getNombre().equalsIgnoreCase(nombre.trim())))
            throw new DatosInvalidosException("Ya existe una sala con ese nombre");
        Sala s = new Sala(BaseDatos.getInstancia().siguienteId("SAL"), nombre.trim(), tipo, filas, columnas);
        salas.guardar(s);
        return s;
    }

    public void actualizar(Sala s, String nombre, TipoSala tipo, int filas, int columnas) {
        Validador.requerido(nombre, "nombre");
        boolean cambiaDistribucion = s.getFilas() != filas || s.getColumnas() != columnas || s.getTipo() != tipo;
        if (cambiaDistribucion && tieneFuncionesFuturas(s))
            throw new DatosInvalidosException("La sala tiene funciones programadas; no se puede cambiar su tipo ni distribución");
        s.setNombre(nombre.trim());
        s.setTipo(tipo);
        if (s.getFilas() != filas || s.getColumnas() != columnas) s.generarAsientos(filas, columnas);
    }

    public void cambiarHabilitacionAsiento(Sala s, Asiento a) {
        if (tieneFuncionesFuturas(s) && a.isHabilitado()
                && funciones.listar().stream().anyMatch(f -> f.getSala().equals(s) && f.getAsientosOcupados().contains(a.getCodigo())
                && f.getFechaHora().isAfter(reloj.ahora())))
            throw new DatosInvalidosException("El asiento está vendido en una función próxima");
        a.setHabilitado(!a.isHabilitado());
    }

    public void eliminar(Sala s) {
        if (funciones.listar().stream().anyMatch(f -> f.getSala().equals(s)))
            throw new DatosInvalidosException("La sala tiene funciones registradas");
        salas.eliminar(s.getId());
    }

    public List<Sala> listar() { return salas.listar(); }

    private boolean tieneFuncionesFuturas(Sala s) {
        return funciones.listar().stream().anyMatch(f -> f.getSala().equals(s)
                && f.getEstado() == EstadoFuncion.PROGRAMADA && f.getFechaHora().isAfter(reloj.ahora()));
    }

    public void guardar(Sala s, String nombre, TipoSala tipo, int filas, int columnas) {
        if (s == null) crear(nombre, tipo, filas, columnas);
        else actualizar(s, nombre, tipo, filas, columnas);
    }
}
