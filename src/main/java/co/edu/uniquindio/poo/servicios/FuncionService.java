package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.EstadoFuncion;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.exceptions.FuncionNoDisponibleException;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.model.Sala;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;
import co.edu.uniquindio.poo.util.Reloj;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class FuncionService {
    public static final int MINUTOS_ASEO = 20;
    private final Repositorio<Funcion> funciones;
    private final Reloj reloj;

    public FuncionService(Repositorio<Funcion> funciones, Reloj reloj) {
        this.funciones = funciones;
        this.reloj = reloj;
    }

    public Funcion crear(Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime hora, double precioBase) {
        if (pelicula == null || sala == null || fecha == null || hora == null)
            throw new DatosInvalidosException("Complete película, sala, fecha y hora");
        if (!pelicula.isEnCartelera()) throw new DatosInvalidosException("La película no está en cartelera");
        Validador.positivo(precioBase, "precio");
        Funcion nueva = new Funcion(BaseDatos.getInstancia().siguienteId("FUN"), pelicula, sala, fecha, hora, precioBase);
        if (!nueva.getFechaHora().isAfter(reloj.ahora())) throw new DatosInvalidosException("La función debe programarse en el futuro");
        validarCruce(nueva);
        funciones.guardar(nueva);
        return nueva;
    }

    private void validarCruce(Funcion nueva) {
        for (Funcion f : funciones.listar()) {
            if (f == nueva || !f.getSala().equals(nueva.getSala()) || f.getEstado() == EstadoFuncion.CANCELADA) continue;
            boolean cruza = nueva.getFechaHora().isBefore(f.getFechaHoraFin().plusMinutes(MINUTOS_ASEO))
                    && f.getFechaHora().isBefore(nueva.getFechaHoraFin().plusMinutes(MINUTOS_ASEO));
            if (cruza) throw new DatosInvalidosException("La sala ya tiene una función en ese horario: " + f);
        }
    }

    public void marcarCancelada(Funcion f) {
        if (f.getEstado() == EstadoFuncion.CANCELADA) throw new FuncionNoDisponibleException("La función ya estaba cancelada");
        if (!f.getFechaHora().isAfter(reloj.ahora())) throw new FuncionNoDisponibleException("La función ya inició o finalizó");
        f.setEstado(EstadoFuncion.CANCELADA);
    }

    public void eliminar(Funcion f) {
        if (!f.getAsientosOcupados().isEmpty()) throw new DatosInvalidosException("La función tiene entradas vendidas; cancélela en su lugar");
        funciones.eliminar(f.getId());
    }

    public List<Funcion> listar() {
        return funciones.listar().stream().sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
    }

    public List<Funcion> listarRecientesPrimero() {
        return funciones.listar().stream().sorted(Comparator.comparing(Funcion::getFechaHora).reversed()).toList();
    }

    public List<Funcion> disponibles(Pelicula pelicula) {
        return listar().stream().filter(f -> f.getPelicula().equals(pelicula))
                .filter(f -> f.estaDisponibleParaVenta(reloj.ahora())).toList();
    }
}
