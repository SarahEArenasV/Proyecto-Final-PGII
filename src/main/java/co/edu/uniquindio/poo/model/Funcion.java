package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.EstadoFuncion;
import co.edu.uniquindio.poo.exceptions.AsientoNoDisponibleException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Setter
public class Funcion implements Identificable {
    @Getter private final String id;
    @Getter @Setter private Pelicula pelicula;
    @Getter @Setter private Sala sala;
    @Getter @Setter private LocalDate fecha;
    @Getter @Setter private LocalTime hora;
    @Getter @Setter private double precioBase;
    @Getter @Setter private EstadoFuncion estado = EstadoFuncion.PROGRAMADA;
    private final Set<String> asientosOcupados = new HashSet<>();

    public Funcion(String id, Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime hora, double precioBase) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fecha = fecha;
        this.hora = hora;
        this.precioBase = precioBase;
    }

    public LocalDateTime getFechaHora() { return LocalDateTime.of(fecha, hora); }
    public LocalDateTime getFechaHoraFin() { return getFechaHora().plusMinutes(pelicula.getDuracionMinutos()); }

    public boolean isAsientoDisponible(String codigo) {
        return !asientosOcupados.contains(codigo) && sala.buscarAsiento(codigo).isHabilitado();
    }

    public void ocuparAsiento(String codigo) {
        if (!isAsientoDisponible(codigo)) throw new AsientoNoDisponibleException("El asiento " + codigo + " ya está ocupado");
        asientosOcupados.add(codigo);
    }

    public int getAsientosDisponibles() {
        return (int) sala.getAsientos().stream().filter(a -> a.isHabilitado() && !asientosOcupados.contains(a.getCodigo())).count();
    }

    public void liberarAsiento(String codigo) { asientosOcupados.remove(codigo); }

    public boolean estaDisponibleParaVenta(LocalDateTime ahora) {
        return estado == EstadoFuncion.PROGRAMADA && getFechaHora().isAfter(ahora);
    }

    public double getPorcentajeOcupacion() {
        int capacidad = sala.getCapacidad();
        return capacidad == 0 ? 0 : asientosOcupados.size() * 100.0 / capacidad;
    }

    public Set<String> getAsientosOcupados() { return Collections.unmodifiableSet(asientosOcupados); }

    @Override public String toString() {
        return pelicula.getTitulo() + " | " + fecha + " " + hora + " | " + sala.getNombre();
    }
}
