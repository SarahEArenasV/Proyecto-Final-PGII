package co.edu.uniquindio.poo.exceptions;

public class FuncionNoDisponibleException extends CinemaException {
    public FuncionNoDisponibleException() { super("La función no está disponible"); }
    public FuncionNoDisponibleException(String mensaje) { super(mensaje); }
}
