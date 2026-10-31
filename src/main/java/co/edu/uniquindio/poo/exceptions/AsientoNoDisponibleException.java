package co.edu.uniquindio.poo.exceptions;

public class AsientoNoDisponibleException extends CinemaException {
    public AsientoNoDisponibleException() { super("El asiento no está disponible"); }
    public AsientoNoDisponibleException(String mensaje) { super(mensaje); }
}
